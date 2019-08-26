package SXT._4IO.part5;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;

/**
 * @program: JavaStudy
 * @description:
 * @chineseDescription: 面向对象的思想封装文件分割的代码，实现代码的可重用
 * @author: LiuDongMan
 * @createdDate: 2019-08-26 15:47
 **/
public class DivideFile02 {
    private File inFile;    // 需要分割的文件对象
    private int singleSize; // 每一块的大小
    private int blockCount; // 分割之后的块数
    private long totalSize; // 文件总大小
    private String destDir; // 输出文件夹路径
    private List<String> destPaths; // 输出文件名

    public DivideFile02(File inFile, int singleSize, String destDir) {
        this.inFile = inFile;
        this.singleSize = singleSize;
        this.totalSize = inFile.length();
        this.blockCount = (int) Math.ceil(this.totalSize * 1.0 / singleSize);
        this.destPaths = new ArrayList<>();
        this.destDir = destDir;

        for (int i = 0; i < this.blockCount; i++) {
            destPaths.add(destDir + "/" + i + "-" + this.inFile.getName());
        }
    }

    public DivideFile02() {
    }

    public void divideFile() {
        int actualSize; // 每一块的实际大小，因为会存在文件或最后一块内容过少的情况
        int startPos;

        for (int i = 0; i < this.blockCount; i++) {
            startPos = i * this.singleSize;

            if (i == this.blockCount - 1) {
                actualSize = (int) this.totalSize;
            } else {
                actualSize = this.singleSize;
                this.totalSize -= actualSize;
            }

            divideDetail(i, startPos, actualSize);
        }
    }

    private void divideDetail(int blockNum, int startPos, int actualSize) {
        try (RandomAccessFile raf01 = new RandomAccessFile(this.inFile, "r");
             RandomAccessFile raf02 = new RandomAccessFile(destPaths.get(blockNum), "rw")) {
            raf01.seek(startPos);

            int length = -1;
            byte[] flush01 = new byte[1024];

            while ((length = raf01.read(flush01)) != -1) {
                if (length > actualSize) {
                    raf02.write(flush01, 0, actualSize);
                    break;
                } else {
                    raf02.write(flush01, 0, length);
                    actualSize -= length;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        DivideFile02 df = new DivideFile02(new File("src/SXT/_4IO/part2/NodeIOTest.java"), 1024, "Dest");
        df.divideFile();
    }
}
