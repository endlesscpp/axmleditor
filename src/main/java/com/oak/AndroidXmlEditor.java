/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.oak;

import com.google.common.primitives.Ints;
import com.oak.XmlDecompressor.Marker;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

public class AndroidXmlEditor {
    private static final String DEFAULT_CLASS_NAME = "AndroidManifest.xml";

    private final File archiveFile;
    private final String xmlName;
    private Marker mMarker;
    private Map<String, String> mKvs2Update;
    private String mOutputXmlPath;

    private XmlDecompressor xmlDecompressor = new XmlDecompressor();

    public AndroidXmlEditor(String xmlName, File archiveFile) {
        this.xmlName = xmlName;
        this.archiveFile = archiveFile;
    }

    public AndroidXmlEditor(File archiveFile) {
        this(DEFAULT_CLASS_NAME, archiveFile);
    }

    public void setMetadata2Modify(Map<String, String> kvs) {
        mKvs2Update = kvs;
        xmlDecompressor.setMetadata2Modify(kvs);
    }

    public void setOutputXmlPath(String fullXmlPath) {
        mOutputXmlPath = fullXmlPath;
    }

    public void apply() {
        if (mOutputXmlPath == null || mOutputXmlPath.isEmpty()) {
            System.out.println("Please set the output xml path");
            return;
        } else {
            System.out.println("The new revised xml is:");
            System.out.println("    " + mOutputXmlPath);
        }

        byte[] finalbuf = null;
        InputStream is = null;
        ZipFile zip = null;
        ByteArrayOutputStream bout = null;
        ByteArrayOutputStream bout2 = null;
        try {
            long size;

            if (archiveFile.getName().endsWith(".apk")
                || archiveFile.getName().endsWith(".zip")
                || archiveFile.getName().endsWith(".aar")) {
                zip = new ZipFile(archiveFile);
                ZipEntry mft = zip.getEntry(xmlName);
                size = mft.getSize();
                is = zip.getInputStream(mft);
            } else {
                size = archiveFile.length();
                is = new FileInputStream(archiveFile);
            }

            if (size > Integer.MAX_VALUE) {
                throw new IOException("File larger than " + Integer.MAX_VALUE + " bytes not supported");
            }

            bout = new ByteArrayOutputStream((int) size);
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = is.read(buffer)) > 0) {
                bout.write(buffer, 0, bytesRead);
            }

            // step1, parse the marker
            try (ByteArrayInputStream bin = new ByteArrayInputStream(bout.toByteArray())) {
                this.mMarker = xmlDecompressor.parseXml4Modify(bin);
            }
            int oldChunkSize = mMarker.stringsChunkInfo.chunkSize;

            // step2, change the marker, serialize the string chunk to a byte array.
            Map<String, Integer> valueIndexMap = new HashMap<>();
            for (Map.Entry<String, String> entry : mKvs2Update.entrySet()) {
                valueIndexMap.put(entry.getValue(), 0);
            }
            for (String str : valueIndexMap.keySet()) {
                mMarker.stringsChunkInfo.numStrings++;
                mMarker.stringsChunkInfo.strings.add(str);
                valueIndexMap.put(str, mMarker.stringsChunkInfo.numStrings - 1);
            }
            mMarker.stringsChunkInfo.stringsStart += valueIndexMap.size() * 4;
            // pay attention to the chunkSize, it's not update at here.

            bout2 = new ByteArrayOutputStream((int) size);
            byte[] stringsChunkBuf = mMarker.stringsChunkInfo.serialize2Stream();

            // step3. construct the whole data.
            bout2.write(bout.toByteArray(), 0, 8);
            bout2.write(stringsChunkBuf);
            bout2.write(bout.toByteArray(), oldChunkSize + 8, bout.size() - oldChunkSize - 8);

            finalbuf = bout2.toByteArray();
            for (Map.Entry<String, Integer> entry : mMarker.name2ValueOffsetMap.entrySet()) {
                int offset = entry.getValue();
                int idx = valueIndexMap.get(mKvs2Update.get(entry.getKey()));
                offset += mMarker.stringsChunkInfo.chunkSize - oldChunkSize;
                // write the idx to steam at offset, in little endian

                byte[] intbuf = Ints.toByteArray(Integer.reverseBytes(idx));
                System.arraycopy(intbuf, 0, finalbuf, offset, intbuf.length);
                // also change the resource id.
                System.arraycopy(intbuf, 0, finalbuf, offset + 8, intbuf.length);
            }

            // update the whole size.
            {
                byte[] buf = Ints.toByteArray(Integer.reverseBytes(finalbuf.length));
                System.arraycopy(buf, 0, finalbuf, 4, buf.length);
            }
        } catch (Exception e) {
            System.err.println("Error reading AndroidManifext.xml " + e.getMessage());
            e.printStackTrace(System.err);
        } finally {
            closeResource(is);
            closeResource(zip);
            closeResource(bout);
            closeResource(bout2);
        }

        // write the finalbuf to new xml.
        if (finalbuf != null) {
            // verify it.
            try (ByteArrayInputStream stm = new ByteArrayInputStream(finalbuf)) {
                this.mMarker = xmlDecompressor.parseXml4Modify(stm);
                int len = mMarker.stringsChunkInfo.chunkSize;
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException("verify failed...");
            }

            try (FileOutputStream stm = new FileOutputStream(mOutputXmlPath)) {
                stm.write(finalbuf);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private static void closeResource(Closeable closeable) {
        if (closeable == null) {
            return;
        }

        try {
            closeable.close();
        } catch (IOException ex) {
            System.err.println("Error closing resource: " + ex.getMessage());
            ex.printStackTrace(System.err);
        }
    }
}
