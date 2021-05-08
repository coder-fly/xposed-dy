package com.spark.xposeddy.util;

import android.text.TextUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 方法都是同步调用，需要在子线程里执行
 */
public class ShellFileUtil {
    private ShellFileUtil() {
        /** cannot be instantiated **/
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    public static boolean exists(String file) {
        return Shell.execCommand("ls " + file, true).result == 0;
    }

    public static boolean exists(File file) {
        return Shell.execCommand("ls " + file.getAbsolutePath(), true).result == 0;
    }

    public static void deleteFile(File file) {
        String name = file.getName();
        if (exists(file)) {
            boolean success = Shell.execCommand("rm -fr " + file.getAbsolutePath(), true).result == 0;
            if (success) {
                TraceUtil.d(name + " 文件删除成功！");
            } else {
                TraceUtil.e(name + " 文件删除失败！");
            }
        } else {
            TraceUtil.d(name + " 文件不存在！");
        }
    }

    public static void createDir(File file) {
        String name = file.getName();
        boolean success = Shell.execCommand("mkdir -p " + file.getAbsolutePath(), true).result == 0;
        if (success) {
            TraceUtil.d(name + " 目录创建成功！");
        } else {
            TraceUtil.e(name + " 目录创建失败！");
        }
    }

    public static void copyFile(File file, File dir) {
        String name = file.getName();
        String targetFile = dir.getAbsolutePath() + "/" + name;

        if (exists(file)) {
            TraceUtil.d(name + " 新文件存在！");
        } else {
            TraceUtil.e(name + " 新文件不存在，复制失败！");
            return;
        }

        if (exists(targetFile)) {
            boolean success = Shell.execCommand("rm -fr " + targetFile, true).result == 0;
            if (success) {
                TraceUtil.d(name + " 老文件删除成功！");
            } else {
                TraceUtil.e(name + " 老文件删除失败！");
                return;
            }
        } else {
            TraceUtil.d(name + " 老文件不存在！");
        }

        // 安卓copy文件，需要尽可能保留文件属性（包括所有者、所属组、权限与时间）
        boolean success = Shell.execCommand("cp -pr " + file.getAbsolutePath() + " " + dir.getAbsolutePath(), true).result == 0;
        if (success) {
            TraceUtil.d(name + " 新文件复制成功！");
        } else {
            TraceUtil.e(name + " 新文件复制失败！");
        }
    }

    public static void copyFileAndCtx(File file, File dir) {
        String name = file.getName();
        String targetFile = dir.getAbsolutePath() + "/" + name;

        if (exists(file)) {
            TraceUtil.d(name + " 新文件存在！");
        } else {
            TraceUtil.e(name + " 新文件不存在，复制失败！");
            return;
        }

        if (exists(targetFile)) {
            boolean success = Shell.execCommand("rm -fr " + targetFile, true).result == 0;
            if (success) {
                TraceUtil.d(name + " 老文件删除成功！");
            } else {
                TraceUtil.e(name + " 老文件删除失败！");
                return;
            }
        } else {
            TraceUtil.d(name + " 老文件不存在！");
        }

        // 安卓copy文件，需要尽可能保留文件属性（包括所有者、所属组、权限与时间）
        boolean success = Shell.execCommand("cp -pr " + file.getAbsolutePath() + " " + dir.getAbsolutePath(), true).result == 0;
        if (success) {
            TraceUtil.d(name + " 新文件复制成功！");
        } else {
            TraceUtil.e(name + " 新文件复制失败！");
        }

        // 安卓copy文件，需要保持文件se上下文一致
        String ctx = readSeCtx(file);
        if (!TextUtils.isEmpty(ctx)) {
            if (setSeCtx(dir.getAbsolutePath() + "/" + name, ctx)) {
                TraceUtil.d(name + " ctx设置成功！");
            } else {
                TraceUtil.e(name + " ctx设置失败");
            }
        }
    }

    public static void moveFile(File file, File dir) {
        String name = file.getName();
        String targetFile = dir.getAbsolutePath() + "/" + name;

        if (exists(file)) {
            TraceUtil.d(name + " 新文件存在！");
        } else {
            TraceUtil.e(name + " 新文件不存在，移动失败！");
            return;
        }

        if (exists(targetFile)) {
            boolean success = Shell.execCommand("rm -fr " + targetFile, true).result == 0;
            if (success) {
                TraceUtil.d(name + " 老文件删除成功！");
            } else {
                TraceUtil.e(name + " 老文件删除失败！");
                return;
            }
        } else {
            TraceUtil.d(name + " 老文件不存在！");
        }

        boolean success = Shell.execCommand("mv -f " + file.getAbsolutePath() + " " + dir.getAbsolutePath(), true).result == 0;
        if (success) {
            TraceUtil.d(name + " 新文件移动成功！");
        } else {
            TraceUtil.e(name + " 新文件移动失败！");
        }
    }

    /**
     * 存在的前提下，判断是否是文件
     * 如果是文件：-rw-rw----
     * 如果是空目录：total 0
     * 如果是非空目录：total n -rw-rw----
     *
     * @param file
     * @return
     */
    public static boolean isFile(File file) {
        Shell.CommandResult result = Shell.execCommand("ls -l " + file.getAbsolutePath(), true);
        if (result.result == 0) {
            if (!TextUtils.isEmpty(result.successMsg)) {
                String[] list = result.successMsg.split("(?=[-dlbcps][-r][-w][-x][-r][-w][-x][-r][-w][-x])");
                String isFileStr = list[0];
                boolean isFile = isFileStr.matches("(.*[-dlbcps][-r][-w][-x][-r][-w][-x][-r][-w][-x].*)");
                TraceUtil.e("isFile: file = " + file.getName() + ", isFile = " + isFile + ", isFileStr = " + isFileStr);
                return isFile;
            }
        }
        return false;
    }

    // 存在的前提下，判断是否是目录
    public static boolean isDirectory(File file) {
        return !isFile(file);
    }

    /**
     * 系统File只能拿到名字，不能使用isFile、isDirectory；除非自定义一个File类
     *
     * @param file
     * @return
     */
    public static List<File> listFiles(File file) {
        List<File> fileList = new ArrayList<>();

        if (isFile(file)) {
            return fileList;
        }

        Shell.CommandResult result = Shell.execCommand("ls -l " + file.getAbsolutePath(), true);
        if (result.result == 0) {
            if (!TextUtils.isEmpty(result.successMsg)) {
                String[] list = result.successMsg.split("(?=[-dlbcps][-r][-w][-x][-r][-w][-x][-r][-w][-x])");
                for (int i = 0; i < list.length; i++) {
                    String property = list[i];
                    boolean isFile = property.matches("(.*[-dlbcps][-r][-w][-x][-r][-w][-x][-r][-w][-x].*)");
                    if (isFile) {
                        String[] arr = property.split("(\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2} )");
                        if (arr.length == 2) {
                            String name = arr[1];
                            fileList.add(new File(file, name));
                        }
                    }
                }
                TraceUtil.e("listFiles: " + fileList.toString());
            }
        }

        return fileList;
    }

    static String readSeCtx(File file) {
        String name = file.getName();
        Shell.CommandResult result = Shell.execCommand("ls -Z " + file.getAbsolutePath(), true);
        if (result.result == 0) {
            TraceUtil.d(name + " properties: " + result.successMsg);
            String ctx = "u:" + StringUtil.getMidText(result.successMsg, "u:", " ");
            TraceUtil.e(name + " ctx: " + ctx);
            return ctx;
        }
        return null;
    }

    static boolean setSeCtx(String file, String ctx) {
        return Shell.execCommand("chcon -R " + ctx + " " + file, true).result == 0;
    }
}
