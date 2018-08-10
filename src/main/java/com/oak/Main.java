package com.oak;

import java.io.File;
import java.util.HashMap;

public class Main {
    public static void main(final String[] args) {
        System.out.println("------------------Begin to run:------------------------");
        if (args.length < 2 || "-h".equals(args[0])) {
            showUsage();
            return;
        }

        HashMap<String, String> mp = new HashMap<>();
        for (int i = 1; i < args.length; i++) {
            String[] kv = args[i].split("=");
            if (kv.length != 2 || kv[0].isEmpty() || kv[1].isEmpty()) {
                showUsage();
                return;
            }
            System.out.println(kv[0] + '=' + kv[1]);
            mp.put(kv[0], kv[1]);
        }

        File archiveFile = new File(args[0]);
        String outputXmlPath = new File(archiveFile.getParentFile(), "AndroidManifest.xml").getPath();
        AndroidXmlEditor editor = new AndroidXmlEditor(archiveFile);
        editor.setMetadata2Modify(mp);
        editor.setOutputXmlPath(outputXmlPath);

        editor.apply();
    }

    private static void showUsage() {
        System.out.println("command line like:");
        System.out.println("java -jar axmleditor.jar sample.apk UMENG_CHANNEL=baidu BD_APP_CHANNEL=baidu CHANNEL=baidu");
    }
}
