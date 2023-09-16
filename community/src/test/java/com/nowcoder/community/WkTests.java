package com.nowcoder.community;

import java.io.IOException;

public class WkTests {
    public static void main(String[] args) {
        String cmd = "/usr/local/bin/wkhtmltoimage --quality 75 https://wkhtmltopdf.org/ /Users/songss/Desktop/Programming/nowcode/data/wk-images/3.png ";
        try {
            Process process = Runtime.getRuntime().exec(cmd);
            int exitCode = process.waitFor();

            if (exitCode == 0) {
                System.out.println("Command executed successfully.");
            } else {
                System.out.println("Command failed with exit code: " + exitCode);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
