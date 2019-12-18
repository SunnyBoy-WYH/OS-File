package com.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

import java.util.Map;

/**
 * 文件模型层
 */
public class FileModel implements Serializable, Cloneable {


    public FileModel() {
    }


    private String name; //文件名或目录名
    private String type; //文件类型
    private int attr; //用来识别是文件还是目录
    private int startNum;    //在FAT表中起始位置
    private int size;    //文件的大小
    private boolean attrIsOnlyRead = false;//标识只读、非只读
    private boolean attrIsHide = false;//标识隐藏、非隐藏



    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    public Map<String, FileModel> getSubMap() {
        return subMap;
    }

    public void setSubMap(Map<String, FileModel> subMap) {
        this.subMap = subMap;
    }

    public Map<String, FileModel> subMap = new HashMap<String, FileModel>();
    private FileModel father = null;    //该文件或目录的上级目录
    private String FileContent; //文件内容

    public String getFileContent() {
        return FileContent;
    }

    public void setFileContent(String fileContent) {
        FileContent = fileContent;
    }


    public FileModel(String name, String type, int startNum, int size) {
        this.name = name;
        this.type = type;
        this.attr = 2;
        this.startNum = startNum;
        this.size = size;
    }

    public FileModel(String name, int startNum) {
        this.name = name;
        this.attr = 3;
        this.startNum = startNum;
        this.type = " ";
        this.size = 1;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getAttr() {
        return attr;
    }

    public void setAttr(int attr) {
        this.attr = attr;
    }

    public int getStartNum() {
        return startNum;
    }

    public void setStartNum(int startNum) {
        this.startNum = startNum;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public FileModel getFather() {
        return father;
    }

    public void setFather(FileModel father) {
        this.father = father;
    }

    public boolean isAttrIsOnlyRead() {
        return attrIsOnlyRead;
    }

    public void setAttrIsOnlyRead(boolean attrIsOnlyRead) {
        this.attrIsOnlyRead = attrIsOnlyRead;
    }

    public boolean isAttrIsHide() {
        return attrIsHide;
    }

    public void setAttrIsHide(boolean attrIsHide) {
        this.attrIsHide = attrIsHide;
    }


}

