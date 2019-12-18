package com.controller;
import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import com.view.UIDesign;
import com.model.FileModel;

/**
 *
 */
public class OSManager {


    private UIDesign uiDesign;

    public Map<String, FileModel> totalFiles = new HashMap<String, FileModel>();
    private int SUM_DISK_SIZE = 128;


    //定义FAT表
    private int[] fat = new int[SUM_DISK_SIZE];

    //创建根目录 使用fat表的第一项
    private FileModel root = new FileModel("root", 1);

    private FileModel nowCatalog = root;


    public OSManager() {
        //将FAT表初始化全部为0，并将第一位设为根目录的空间
        for (int i = 2; i < fat.length; i++) {
            fat[i] = 0;
        }
        fat[1] = 255; //255表示磁盘块已占用
        fat[0] = 126; //纪录磁盘剩余块数
        root.setFather(root);
        totalFiles.put("root", root);
    }

    /**
     * 向fat表申请空间
     * @param size
     * @return
     */
    public int setFat(int size) {
        int[] startNum = new int[128];
        int i = 2; //纪录fat循环定位
        for (int j = 0; j < size; i++) {
            if (fat[i] == 0) {
                startNum[j] = i; //纪录该文件所有磁盘块
                if (j > 0) {
                    fat[startNum[j - 1]] = i; //fat上一磁盘块指向下一磁盘块地址
                }
                j++;
            }
        }
        fat[i - 1] = 255;
        return startNum[0]; //返回该文件起始块盘号
    }

    /**
     *
     * 该方法用于删除时释放FAT表的空间
     */
    public void delFat(int startNum) {
        int nextPoint = fat[startNum];
        int nowPoint = startNum;
        int count = 0;
        while (fat[nowPoint] != 0) {
            nextPoint = fat[nowPoint];
            if (nextPoint == 255) {
                fat[nowPoint] = 0;
                count++;
                break;
            } else {
                fat[nowPoint] = 0;
                count++;
                nowPoint = nextPoint;
            }
        }
        fat[0] += count;
    }
    /**
     *
     * 以下为向文件追加内容方法
     * 追加内容需要打开文件后才能操作
     */

    public void reAdd(String name, int addSize) {

        if (fat[0] >= addSize) {
            nowCatalog = nowCatalog.getFather();
            if (nowCatalog.subMap.containsKey(name)) {
                FileModel value = nowCatalog.subMap.get(name);
                if (value.getAttr() == 2) {
                    value.setSize(value.getSize() + addSize);
                    reAddFat(value.getStartNum(), addSize);
                    System.out.println("追加内容成功！正在重新打开文件...");
                    openFile(name);
                } else {
                    System.out.println("追加内容失败，请确认文件名是否正确输入。");
                }
            } else {
                System.out.println("追加内容失败，请确认文件名是否正确输入！");
                showFile();
            }
        } else {
            System.out.println("追加内容失败，内存空间不足！");
        }
    }
    /**
     *
     * 以下为追加内容时修改fat表
     *
     */

    public void reAddFat(int startNum, int addSize) {
        int nowPoint = startNum;
        int nextPoint = fat[startNum];
        while (fat[nowPoint] != 255) {
            nowPoint = nextPoint;
            nextPoint = fat[nowPoint];
        }//找到该文件终结盘块

        for (int i = 2, count = 0; count < addSize; i++) {
            if (fat[i] == 0) {
                fat[nowPoint] = i;
                nowPoint = i;
                count++;
                fat[nowPoint] = 255;//作为当前文件终结盘块
            }
        }
    }



    /**
     将文件保存到本地E盘
     */
    public void writeToFile() {

        File file = new File("E:\\IDEA CODE\\OS\\test.txt");
        FileOutputStream out;
        try {
            out = new FileOutputStream(file);
            ObjectOutputStream objOut = new ObjectOutputStream(out);
            objOut.writeObject(totalFiles);
            objOut.flush();

            objOut.writeObject(fat);
            objOut.flush();

            objOut.close();
            System.out.println("write object success!");
        } catch (IOException e) {
            System.out.println("write object failed");
            e.printStackTrace();

        }
    }



    /**
     * 将结果从上次修改的文件中读取出来
     */
    public void readFile() {

        File file = new File("E:\\IDEA CODE\\OS\\test.txt");
        if (file.exists()) {
            FileInputStream fileInputStream = null;
            try {
                fileInputStream = new FileInputStream(file);
                ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
                Map<String, FileModel> map = (Map<String, FileModel>) objectInputStream.readObject();
                totalFiles = map;
                root = map.get("root");
                fat = (int[]) objectInputStream.readObject();
                objectInputStream.close();
                System.out.println("read object success!");
            } catch (IOException | ClassNotFoundException e) {
                System.out.println("read object failed");
                e.printStackTrace();

            }
        } else {
            uiDesign.showDialog("你上次没有任何备份！");
        }

    }

    /**
     * 以下为创建文件方法
     */
    public void createFile(String name, String type, String contents) {
        int byteSize = contents.length() / 2;

        int size = byteSize + 8 > 64 ? (byteSize % 64 + 1) : 1;//若大于64字节则占用1磁盘块，否则占用相应多的磁盘块
        if (fat[0] >= size) {    //判断磁盘剩余空间是否足够建立文件
            FileModel value = nowCatalog.subMap.get(name); //该目录下是否寻找同名目录或文件
            if (value != null) {  //判断该文件是否存在
                if (value.getAttr() == 3) {   //若存在同名目录 继续创建文件
                    int startNum = setFat(size);
                    FileModel file = new FileModel(name, type, startNum, size);
                    file.setFileContent(contents);
                    file.setFather(nowCatalog); //纪录上一层目录
                    nowCatalog.subMap.put(name, file); //在父目录添加该文件
                    totalFiles.put(file.getName(), file);

                    fat[0] -= size;
                    System.out.println("创建文件成功！");
                    showFile();
                    uiDesign.refreshTree();
                } else if (value.getAttr() == 2) { //若同名文件已存在，创建失败
                    System.out.println("创建失败，该文件已存在！");
                    uiDesign.showConfirmDialog(1);
                    showFile();
                }
            } else if (value == null) { //若无同名文件或文件夹，继续创建文件
                int startNum = setFat(size);
                FileModel file = new FileModel(name, type, startNum, size);
                file.setFather(nowCatalog); //纪录上一层目录
                file.setFileContent(contents);
                nowCatalog.subMap.put(name, file); //在父目录添加该文件
                totalFiles.put(file.getName(), file);
                fat[0] -= size;
                System.out.println("创建文件成功......");
                showFile();
                uiDesign.refreshTree();
            }
        } else {
            System.out.println("创建文件失败，磁盘空间不足！");
        }

    }

    /**
     * 创建目录的方法
     *
     * @param name
     * @return
     */
    public boolean createCatolog(String name) {

        if (fat[0] >= 1) { //判断磁盘空间是否足够创建文件夹

            FileModel value = nowCatalog.subMap.get(name); //判断该目录下是否存在同名目录或文件

            if (value != null) {
                //存在同名文件而不是目录
                if (value.getAttr() == 2) {
                    int startNum = setFat(1);//获取该目录在fat表中的初始磁盘块位置
                    FileModel catalog = new FileModel(name, startNum);
                    catalog.setFather(nowCatalog); //纪录上一层目录
                    nowCatalog.subMap.put(name, catalog);//将新建的目录放到当前目录的子目录容器下
                    fat[0]--;
                    totalFiles.put(catalog.getName(), catalog);
                    System.out.println("创建目录成功...");
                    showFile();
                    uiDesign.refreshTree();//刷新目录树
                    return true;
                } else if (value.getAttr() == 3) {
                    System.out.println("创建目录失败，该目录已存在！");
                    showFile();
                    return false;
                }
            } else if (value == null) {
                int startNum = setFat(1);
                FileModel catalog = new FileModel(name, startNum);
                catalog.setFather(nowCatalog); //纪录上一层目录
                nowCatalog.subMap.put(name, catalog);
                //可分配的磁盘块数-1
                fat[0]--;
                totalFiles.put(catalog.getName(), catalog);
                System.out.println("创建目录成功......");
                showFile();
                uiDesign.refreshTree();
                return true;
            }
        } else {
            System.out.println("创建目录失败，磁盘空间不足！");
            return false;
        }
        return false;
    }


    /**
     *
     * 以下为显示该目录下的所有文件信息
     *
     */

    public void showFile() {
        System.out.println("***************** < " + nowCatalog.getName() + " > *****************");

        if (!nowCatalog.subMap.isEmpty()) {
            for (FileModel value : nowCatalog.subMap.values()) {
                if (value.getAttr() == 3) { //目录文件
                    System.out.println("文件名 : " + value.getName());
                    System.out.println("操作类型 ： " + "文件夹");
                    System.out.println("起始盘块 ： " + value.getStartNum());
                    System.out.println("大小 : " + value.getSize());
                    System.out.println("<-------------------------------------->");
                } else if (value.getAttr() == 2) {
                    System.out.println("文件名 : " + value.getName() + "." + value.getType());
                    System.out.println("操作类型 ： " + "可读可写文件");
                    System.out.println("起始盘块 ： " + value.getStartNum());
                    System.out.println("大小 : " + value.getSize());
                    System.out.println("<-------------------------------------->");
                }
            }
        }
        for (int i = 0; i < 2; i++)
            System.out.println();
        System.out.println("磁盘剩余空间 ：" + fat[0] + "            " + "退出系统请输入exit");
        System.out.println();
    }

    /**
     *
     * 以下为删除该目录下某个文件
     *
     */
    public void deleteFile(String name) {

        //获取当前文件对象
        FileModel value = nowCatalog.subMap.get(name);
        if (value == null) {
            uiDesign.showDialog("删除失败，没有该文件或文件夹!");
        } else if (!value.subMap.isEmpty()) {
            uiDesign.showDialog("删除失败，该文件夹内含有文件！");
        } else {
            //从当前目录中删除该文件对象
            nowCatalog.subMap.remove(name);
            //释放FAT表
            delFat(value.getStartNum());
            if (value.getAttr() == 3) {
                uiDesign.showDialog("文件夹 " + value.getName() + " 已成功删除");
                uiDesign.refreshTree();
                showFile();


            } else if (value.getAttr() == 2) {
                uiDesign.showDialog("文件 " + value.getName() + " 已成功删除");
                uiDesign.refreshTree();
                showFile();

            }
        }
    }

    /**
     * 删除非空目录
     * @param name
     */
    public void deleteNotNullCatalog(String name) {
        FileModel value = nowCatalog.subMap.get(name);
        if (value == null) {
            uiDesign.showDialog("删除失败，没有该文件或文件夹!");
        } else if (!value.subMap.isEmpty()) {
            for (String key : value.getSubMap().keySet()) {
                FileModel fileModel = value.getSubMap().get(key);
                delFat(fileModel.getStartNum());
            }
            nowCatalog.subMap.remove(name);
            delFat(value.getStartNum());
            uiDesign.showDialog("删除非空目录成功!");
            uiDesign.refreshTree();
        } else {
            deleteFile(name);
            uiDesign.refreshTree();
            delFat(value.getStartNum());
        }
    }

    /**
     *
     * 以下为文件或文件夹重命名方法(必须要到某个命令下)
     *
     */

    public void reName(String name, String newName) {
        if (nowCatalog.subMap.containsKey(name)) {
            if (nowCatalog.subMap.containsKey(newName)) {
                System.out.println("重命名失败，同名文件已存在！");
                uiDesign.showDialog("重命名失败，同名文件已存在！");
                showFile();
            } else {
                //nowCatalog.subMap.get(name).setName(newName);
                FileModel value = nowCatalog.subMap.get(name);
                value.setName(newName);
                nowCatalog.subMap.remove(name);
                nowCatalog.subMap.put(newName, value);
                System.out.println("重命名成功！");
                uiDesign.showDialog("重命名成功！");
                uiDesign.refreshTree();
                showFile();
            }
        } else {
            System.out.println("重命名失败，没有该文件！");
            uiDesign.showDialog("重命名失败，没有该文件！");
            showFile();
        }
    }
    /**
     * 以下为打开文件或文件夹方法  -通过命令行时
     *
     */

    public FileModel openFile(String name) {
        if (nowCatalog.subMap.containsKey(name)) {
            FileModel value = nowCatalog.subMap.get(name);
            if (value.getAttr() == 2) {

                System.out.println("文件已打开，文件大小为 : " + value.getSize());
            } else if (value.getAttr() == 3) {
                nowCatalog = value;
                System.out.println("文件夹已打开！");
                //更新目录
                updatePathString();
                showFile();
            }
            return value;
        } else {
            uiDesign.showDialog("没有找到名为" + name + "的文件，请检查命令！");
            return null;
        }
    }

    /**
     *
     * 更新路径
     */
    public void updatePathString() {
        FileModel temp = nowCatalog;
        String str = "";
        Stack<String> stringStack = new Stack<>();

        while (temp != root) {
            stringStack.push(temp.getName() + '/');
            temp = temp.getFather();
        }
        str += "root/";
        while (!stringStack.empty()) {
            String peek = stringStack.peek();
            stringStack.pop();
            str += peek;
        }

        uiDesign.refreshPath(str);

    }

    /**
     * 改变文件属性
     * @param name
     * @param i
     */
    public void change(String name, int i) {
        if (!nowCatalog.getSubMap().containsKey(name)) {
            uiDesign.showDialog("该文件不存在，请检查您的命令！");
        } else {
            FileModel fileModel = nowCatalog.getSubMap().get(name);
            String flag = "";
            switch (i) {
                case 1:
                    fileModel.setAttrIsOnlyRead(true);
                    flag = "只读";
                    break;
                case 2:
                    fileModel.setAttrIsOnlyRead(false);
                    flag = "非只读";
                    break;
                case 3:
                    fileModel.setAttrIsHide(true);
                    flag = "隐藏";
                    break;
                case 4:
                    fileModel.setAttrIsHide(false);
                    flag = "非隐藏";
                    break;
            }
            uiDesign.refreshTree();
            uiDesign.showDialog("修改文件属性成功！修改之后的属性为" + flag);

        }

    }




    /**
     *
     * 以下为返回上一层目录
     *
     */

    public void backFile() {
        if (nowCatalog.getFather() == null) {
            System.out.println("该文件没有上级目录！");
        } else {
            nowCatalog = nowCatalog.getFather();

            showFile();
        }
    }

    /**
     * 以下根据绝对路径寻找文件
     *
     */

    public void searchFile(String[] roadName) {

        FileModel theCatalog = nowCatalog; //设置断点纪录当前目录

        if (totalFiles.containsKey(roadName[roadName.length - 1])) { //检查所有文件中有无该文件

            nowCatalog = root; //返回根目录
            if (nowCatalog.getName().equals(roadName[0])) {    //判断输入路径的首目录是否root
                for (int i = 1; i < roadName.length; i++) {
                    if (nowCatalog.subMap.containsKey(roadName[i])) {

                        nowCatalog = nowCatalog.subMap.get(roadName[i]); //一级一级往下查

                    } else {
                        System.out.println("找不到该路径下的文件或目录，请检查路径是否正确");
                        nowCatalog = theCatalog;
                        showFile();
                        break;
                    }
                }
            } else {
                nowCatalog = theCatalog;
                uiDesign.showDialog("请输入正确的绝对路径！");
                System.out.println("请输入正确的绝对路径！");
                showFile();
            }
        } else {
            uiDesign.showDialog("该文件或目录不存在，请输入正确的绝对路径！");
            System.out.println("该文件或目录不存在，请输入正确的绝对路径！");
            showFile();
        }
    }

    /**
     * 以下为控制台打印FAT表内容
     *
     */
    public void showFAT() {

        for (int j = 0; j < 125; j += 5) {
            System.out.println("第几项 | " + j + "        " + (j + 1) + "        " + (j + 2) + "        "
                    + (j + 3) + "        " + (j + 4));
            System.out.println("内容    | " + fat[j] + "        " + fat[j + 1] + "        " + fat[j + 2]
                    + "        " + fat[j + 3] + "        " + fat[j + 4]);
            System.out.println();
        }
        int j = 125;
        System.out.println("第几项 | " + j + "        " + (j + 1) + "        " + (j + 2));
        System.out.println("内容    | " + fat[j] + "        " + fat[j + 1] + "        " + fat[j + 2]);
        System.out.println();
        showFile();
    }

    /**
     * 用于格式化磁盘
     */
    public void formatRoot() {
        totalFiles.clear();//清空保存所有文件的map
        getFat()[0] = 126;//将磁盘块大小重置为126

        //重置根目录
        FileModel root = new FileModel("root", 1);
        totalFiles.put("root", root);
        setRoot(root);
        //重置FAT表
        for (int i = 0; i < fat.length; i++) {
            if (i >= 2) {
                fat[i] = 0;
            }
        }
    }

    public String getHelpInstrutions() {

        return "命令如下（空格不能省略）：\n" +
                "create FileName  \n" +
                "<创建文件 如：create marco  默认为txt文件 >\n\n" +
                "makdir Name\n" +
                "<创建目录 如：makdir mydir >\n\n" +
                "edit Name\n" +
                "<打开文件 如：open marco 需要在相关目录下>\n\n" +
                "cd CatalogName\n" +
                "<打开目录 如： cd myFile >\n\n" +
                "cd..\n" +
                "<返回上级目录 如： cd..\n" +
                "delete FileName/CatalogName\n" +
                "<删除文件或目录（目录必须为空）如：delete marco >\n" +
                "rename oldName NewName\n" +
                "<重命名文件或目录 如： rename myfile mycomputer >\n" +
                "chadir name \n" +
                "<根据绝对路径寻找文件或者目录 如： chadir root/marco >\n\n" +
                "change fileName 1/2/3/4 对应 只读、非只读、隐藏、非隐藏\n\n" +
                "deldir 目录" +
                "<删除目录，非空目录也可以 如： deldir name >\n\n" +
                "rdir 空目录" +
                "<删除目录,必须是空目录 如： rdir name >\n\n" +
                "<查看FAT表 如： showFAT>\n" +
                "showFAT\n";


    }


    /**
     * @param str
     * @return 被分割的字符串
     */
    public static String[] editStr(String str) {
        Pattern pattern = Pattern.compile("([a-zA-Z0-9.\\\\/]*) *");// 根据空格分割输入命令
        Matcher m = pattern.matcher(str);
        ArrayList<String> list = new ArrayList<String>();
        while (m.find()) {
            list.add(m.group(1));
        }
        String[] strs = list.toArray(new String[list.size()]);

        for (int i = 1; i < strs.length; i++) { // 判断除命令以外每一个参数中是否含有 "."
            int j = strs[i].indexOf(".");
            if (j != -1) { // 若含有"." 将其切割 取前部分作为文件名
                String[] index = strs[i].split("\\."); // 使用转义字符"\\."
                strs[i] = index[0];
            }
        }
        return strs;
    }


    public FileModel getRoot() {
        return root;
    }

    public void setRoot(FileModel root) {
        this.root = root;
    }

    public UIDesign getUiDesign() {
        return uiDesign;
    }

    public void setUiDesign(UIDesign uiDesign) {
        this.uiDesign = uiDesign;
    }

    public void setTotalFiles(Map<String, FileModel> totalFiles) {
        this.totalFiles = totalFiles;
    }

    public int getSUM_DISK_SIZE() {
        return SUM_DISK_SIZE;
    }

    public void setSUM_DISK_SIZE(int SUM_DISK_SIZE) {
        this.SUM_DISK_SIZE = SUM_DISK_SIZE;
    }

    public FileModel getNowCatalog() {
        return nowCatalog;
    }

    public void setNowCatalog(FileModel nowCatalog) {
        this.nowCatalog = nowCatalog;
    }

    public int[] getFat() {
        return fat;
    }

    public void setFat(int[] fat) {
        this.fat = fat;
    }

    public Map<String, FileModel> getTotalFiles() {
        return totalFiles;
    }

}
