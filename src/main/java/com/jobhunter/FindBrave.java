package com.jobhunter;

import java.io.File;

public class FindBrave {
    public static void main(String[] args) {
        System.out.println("🔍 Searching for Brave Browser...\\n");

        String os = System.getProperty("os.name").toLowerCase();
        System.out.println("Operating System: " + os);

        if (os.contains("win")) {
            checkWindows();
        } else if (os.contains("mac")) {
            checkMac();
        } else {
            checkLinux();
        }
    }

    private static void checkWindows() {
        String[] paths = {
                "C:\\\\Program Files\\\\BraveSoftware\\\\Brave-Browser\\\\Application\\\\brave.exe",
                "C:\\\\Program Files (x86)\\\\BraveSoftware\\\\Brave-Browser\\\\Application\\\\brave.exe",
                System.getenv("LOCALAPPDATA") + "\\\\BraveSoftware\\\\Brave-Browser\\\\Application\\\\brave.exe",
                System.getenv("PROGRAMFILES") + "\\\\BraveSoftware\\\\Brave-Browser\\\\Application\\\\brave.exe",
                System.getenv("PROGRAMFILES(X86)") + "\\\\BraveSoftware\\\\Brave-Browser\\\\Application\\\\brave.exe"
        };

        checkPaths(paths);
    }

    private static void checkMac() {
        String[] paths = {
                "/Applications/Brave Browser.app/Contents/MacOS/Brave Browser",
                System.getProperty("user.home") + "/Applications/Brave Browser.app/Contents/MacOS/Brave Browser"
        };

        checkPaths(paths);
    }

    private static void checkLinux() {
        String[] paths = {
                "/usr/bin/brave-browser",
                "/usr/bin/brave",
                "/snap/bin/brave",
                "/opt/brave.com/brave/brave-browser",
                "/opt/brave/brave"
        };

        checkPaths(paths);
    }

    private static void checkPaths(String[] paths) {
        boolean found = false;

        for (String path : paths) {
            File file = new File(path);
            if (file.exists()) {
                System.out.println("✅ FOUND: " + path);
                found = true;
            } else {
                System.out.println("❌ Not at: " + path);
            }
        }

        if (!found) {
            System.out.println("\\n⚠️ Brave not found in standard locations!");
            System.out.println("Please manually locate brave.exe/brave and update the path.");
        }
    }
}
