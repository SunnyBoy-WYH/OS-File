package com.view;

import com.Listener.JTextFieldHintListener;
import com.model.FileModel;
import com.controller.OSManager;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * 2019-12-7 武跃航
 * 该类主要用于界面层
 */
public class UIDesign {

    private JFrame jFrame;
    private JPanel jPanel;

    private JButton button1, button2, buttonFlagRed, buttonFlagGreen;
    private JTextField jtextField, jTextFieldPath;
    private JLabel jLabel, jLabelCata, jLabelDiskShow, jLabelFlagGreen, jLabelFlagRed;
    private JTable jTable;
    private JPanel jPanelCenter, jPanelNorth, jPanelSouth, jPanelShowSpace;
    DefaultTreeModel treeModelPointed;
    private OSManager osManager;
    private JTree tree;
    public ArrayList<String> gradeNames = new ArrayList<>();
    private FileModel Fileroot;
    DefaultMutableTreeNode root;
    private ArrayList<JButton> JbuttonList = new ArrayList<>();
    private JMenuBar jMenuBar;
    private JMenu jMenu;
    private JMenuItem item1, item2;
    private FileModel BufferFileModel = null;    //复制粘贴文件时的文件缓冲区


    public OSManager getOsManager() {
        return osManager;
    }


    public UIDesign(OSManager osManager) {
        this.osManager = osManager;
        Fileroot = osManager.getRoot();
        initView();
        startUpdateThread();

    }

    public void initView() {
        jFrame = new JFrame("操作系统课程设计-武跃航");
        jPanel = new JPanel();
        jPanelShowSpace = new JPanel();
        jLabelCata = new JLabel("当前路径");
        jLabel = new JLabel("欢迎来到文件管理模拟系统");
        jLabelDiskShow = new JLabel("磁盘空间占用情况");
        jTable = new JTable(13, 10);


        jTextFieldPath = new JTextField(30);
        jPanelCenter = new JPanel();
        jPanelNorth = new JPanel();
        jMenuBar = new JMenuBar();
        jMenu = new JMenu("文件");
        item1 = new JMenuItem("导入上次修改");
        item2 = new JMenuItem("退出");
        buttonFlagRed = new JButton();
        buttonFlagGreen = new JButton();
        jLabelFlagGreen = new JLabel("空闲磁盘块");
        jLabelFlagRed = new JLabel("已占用磁盘块");

        jLabelFlagRed.setFont(new Font("黑体", Font.PLAIN, 20));
        jLabelFlagGreen.setFont(new Font("黑体", Font.PLAIN, 20));

        jLabelFlagRed.setBounds(700, 200, 120, 20);
        jLabelFlagGreen.setBounds(700, 260, 120, 20);
        jPanelShowSpace.add(jLabelFlagGreen);
        jPanelShowSpace.add(jLabelFlagRed);

        buttonFlagGreen.setBackground(Color.GREEN);
        buttonFlagGreen.setBounds(860, 200, 70, 20);
        jPanelShowSpace.add(buttonFlagGreen);


        buttonFlagRed.setBackground(Color.red);
        buttonFlagRed.setBounds(860, 260, 70, 20);

        jPanelShowSpace.add(buttonFlagRed);


        jMenuBar.add(jMenu);
        jMenu.setFont(new Font("黑体", Font.PLAIN, 20));
        item1.setFont(new Font("黑体", Font.PLAIN, 20));
        item2.setFont(new Font("黑体", Font.PLAIN, 20));
        jMenu.add(item1);
        jMenu.add(item2);
        jFrame.setJMenuBar(jMenuBar);
        ActionListener l = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JMenuItem jMenuItem = (JMenuItem) e.getSource();
                if (jMenuItem.equals(item1)) {
                    osManager.readFile();
                    osManager.setNowCatalog(osManager.getRoot());
                    refreshTree();

                } else if (jMenuItem.equals(item2)) {
                    System.exit(0);
                }
            }
        };
        item1.addActionListener(l);
        item2.addActionListener(l);


        /**
         *   确定按钮以及查看命令帮助按钮
         */

        button1 = new JButton("确定");
        button2 = new JButton("查看命令帮助");
        button1.setBounds(430, 50, 70, 40);
        button1.setFont(new Font("黑体", Font.PLAIN, 15));
        button2.setBounds(570, 50, 150, 40);
        button2.setFont(new Font("黑体", Font.PLAIN, 15));

        button1.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {


                try {
                    DoInstructions(jtextField.getText().toString());
                } catch (ArrayIndexOutOfBoundsException e1) {
                    e1.printStackTrace();
                    JOptionPane.showMessageDialog(null, "请检查命令！", "提示", JOptionPane.PLAIN_MESSAGE);
                }


            }
        });

        button2.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFrame jFrame = new JFrame();
                JTextArea jTextArea = new JTextArea();
                JScrollPane jScrollPane = new JScrollPane(jTextArea);
                jFrame.getContentPane().add(jScrollPane);
                jTextArea.setText(osManager.getHelpInstrutions());
                jTextArea.setEditable(false);
                Font font = new Font("黑体", Font.PLAIN, 20);

                jTextArea.setFont(font);
                jFrame.setBounds(((Toolkit.getDefaultToolkit().getScreenSize().width) / 2) - 100,
                        ((Toolkit.getDefaultToolkit().getScreenSize().height) / 2) - 200, 500, 500);
                jFrame.setVisible(true);

            }
        });


        Font font = new Font("黑体", Font.PLAIN, 30);
        jLabel.setFont(font);
        jLabel.setBounds(100, 100, 50, 200);


        jTextFieldPath.setText((osManager.getNowCatalog().getName()));
        jTextFieldPath.setFont(new Font("黑体", Font.PLAIN, 20));
        jTextFieldPath.setBounds(180, 0, 540, 30);
        jTextFieldPath.setFont(new Font("黑体", Font.PLAIN, 15));


        jLabelCata.setBounds(80, 0, 100, 30);
        jLabelCata.setFont(new Font("黑体", Font.PLAIN, 20));


        jtextField = new JTextField(30);

        jtextField.setText("请输入您的指令");

        jtextField.setBounds(80, 50, 300, 40);

        jtextField.addFocusListener(new JTextFieldHintListener(jtextField, "请输入你的命令..."));

        jPanelCenter.add(jtextField);
        jPanelCenter.setLayout(null);
        jPanelCenter.setBounds(150, 70, 1200, 600);


        /////////////////////////////////////////////////
        jPanelNorth.add(jLabel);
        jPanelNorth.setBounds(0, 0, 1150, 70);
        jPanel.setLayout(null);
        jPanel.add(jPanelNorth);
        jPanel.add(jPanelCenter);


        jPanelCenter.add(jLabelCata);
        jPanelCenter.add(jTextFieldPath);

        jPanelCenter.add(button1);
        jPanelCenter.add(button2);


        jPanelShowSpace.setBounds(50, 120, 1100, 500);
        jPanelShowSpace.setLayout(null);
        jPanelShowSpace.add(jLabelDiskShow);
        jLabelDiskShow.setBounds(30, 0, 200, 20);
        jLabelDiskShow.setFont(new Font("黑体", Font.PLAIN, 20));
        jPanelCenter.add(jPanelShowSpace);

        /**
         *   展示磁盘空间区，使用了128个按钮(Button类)
         */
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 13; j++) {
                JButton jButton = new JButton();
                JbuttonList.add(jButton);
                jPanelShowSpace.add(jButton);
                jButton.setBounds(30 + j * 50, 30 + i * 42, 40, 30);
            }
        }
        for (int i = 0; i < 11; i++) {
            JButton jButton = new JButton();
            JbuttonList.add(jButton);
            jPanelShowSpace.add(jButton);

            jButton.setBounds(30 + i * 50, 320 + 16 + 70, 40, 30);
        }


        /**
         * 文件目录树的设计
         */
        root = new DefaultMutableTreeNode("root");
        BuildJTree(osManager.getRoot(), root);

        treeModelPointed = new DefaultTreeModel(root, true);
        tree = new JTree(treeModelPointed);


        //树的点击监听器
        MouseListener ml = new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    //悬浮菜单
                    JPopupMenu jPopupMenu = new JPopupMenu();

                    //为悬浮菜单添加菜单项
                    JMenuItem jMenuItem1 = new JMenuItem("打开");
                    JMenuItem jMenuItem2 = new JMenuItem("删除");
                    JMenuItem jMenuItem3 = new JMenuItem("复制");
                    JMenuItem jMenuItem4 = new JMenuItem("粘贴");
                    JMenuItem jMenuItem5 = new JMenuItem("重命名");
                    JMenuItem jMenuItem6 = new JMenuItem("剪切");

                    //获取鼠标右键点击文件的路径
                    TreePath selPath = tree.getPathForLocation(e.getX(), e.getY());
                    String[] strs=null;
                    if(selPath!=null)
                    {
                        Object[] arr = selPath.getPath();
                        System.out.println(Arrays.toString(arr));
                        strs= new String[arr.length];
                        for (int i = 0; i < arr.length; i++) {
                            strs[i] = arr[i].toString();
                        }
                    }


                    //把点击文件的路径作为参数给到监听器
                    jMenuItem1.addActionListener(new ItemClickListener(strs));
                    jMenuItem2.addActionListener(new ItemClickListener(strs));
                    jMenuItem3.addActionListener(new ItemClickListener(strs));
                    jMenuItem4.addActionListener(new ItemClickListener(strs));
                    jMenuItem5.addActionListener(new ItemClickListener(strs));
                    jMenuItem6.addActionListener(new ItemClickListener(strs));


                    //添加菜单项
                    jPopupMenu.add(jMenuItem1);
                    jPopupMenu.add(jMenuItem2);
                    jPopupMenu.add(jMenuItem3);
                    jPopupMenu.add(jMenuItem4);
                    jPopupMenu.add(jMenuItem5);
                    jPopupMenu.add(jMenuItem6);

                    //显示悬浮菜单
                    jPopupMenu.show(e.getComponent(), e.getX(), e.getY());

                }

            }
        };
        //为树添加鼠标监听
        tree.addMouseListener(ml);
        tree.setPreferredSize(new Dimension(150, 50));
        jPanel.add(tree);
        tree.setBounds(0, 70, 150, 500);
        tree.setFont(new Font("黑体", Font.PLAIN, 20));


        jPanelSouth = new JPanel();
        jPanel.add(jPanelSouth, BorderLayout.SOUTH);
        jFrame.setContentPane(jPanel);
        jFrame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                super.windowClosing(e);
                if (JOptionPane.showConfirmDialog(null,
                        "您要保存修改吗吗", "确认", JOptionPane.YES_NO_OPTION) == 0) {
                    osManager.writeToFile();
                }
            }
        });
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        jFrame.setBounds(((Toolkit.getDefaultToolkit().getScreenSize().width) / 2) - 300,
                ((Toolkit.getDefaultToolkit().getScreenSize().height) / 2) - 300, 1200, 750);
        jFrame.setVisible(true);

    }

    /**
     * 右键点击文件目录树进行操作时的监听器
     */
    private class ItemClickListener implements ActionListener {
        String[] path = null;

        public ItemClickListener(String[] path) {
            this.path = path;
        }
        @Override
        public void actionPerformed(ActionEvent e) {
            //根据path获取文件对象
            JMenuItem jMenuItem = (JMenuItem) e.getSource();
            FileModel fileModel = osManager.getRoot();
            for (int i = 1; i < path.length; i++) {
                if (fileModel.getSubMap().containsKey(path[i])) {
                    fileModel = fileModel.getSubMap().get(path[i]);
                }
            }
            switch (jMenuItem.getText()) {
                case "打开":
                    if (fileModel.getAttr() == 3) {
                        showDialog("您要打开的是目录，如果想要进入目录，可以通过cd 目录名");

                    } else if (fileModel.getAttr() == 2) {
                        showTextFrame(fileModel);
                    }
                    break;
                case "复制":
                    System.out.println("用户点击了复制");
                    BufferFileModel = fileModel;
                    break;
                case "剪切":
                    System.out.println("用户点击了剪切");
                    //构造新文件对象
                    FileModel file1 = new FileModel(fileModel.getName(), fileModel.getType(), fileModel.getStartNum(), fileModel.getSize());//深拷贝构造文件对象
                    file1.setFather(fileModel.getFather());
                    file1.setFileContent(fileModel.getFileContent());//复制文件内容
                    //删除原来文件
                    fileModel.getFather().getSubMap().remove(fileModel.getName());
                    osManager.getTotalFiles().remove(fileModel.getName());
                    osManager.delFat(fileModel.getStartNum());
                    refreshTree();

                    BufferFileModel = file1;

                    break;
                case "删除":
                    if (fileModel.getAttr() == 2) {
                        fileModel.getFather().getSubMap().remove(fileModel.getName());
                        osManager.getTotalFiles().remove(fileModel.getName());
                        osManager.delFat(fileModel.getStartNum());
                        System.out.println("文件已删除！");
                        showDialog("文件已删除！");
                        refreshTree();
                    } else if (fileModel.getAttr() == 3) {
                        showDialog("您要删除的是目录，请通过命令行删除！");
                    }
                    break;
                case "重命名":
                    JFrame jFrame = new JFrame();
                    jFrame.getContentPane().setLayout(null);
                    JLabel jLabel = new JLabel("请输入新的文件名");
                    jLabel.setFont(new Font("黑体", Font.PLAIN, 20));
                    jFrame.getContentPane().add(jLabel);
                    jLabel.setBounds(0, 0, 400, 30);
                    JTextField jTextField = new JTextField();
                    jTextField.setFont(new Font("黑体", Font.PLAIN, 20));
                    jTextField.setBounds(30, 30, 330, 30);
                    jFrame.getContentPane().add(jTextField);
                    JButton jButton = new JButton("确定");
                    jButton.setFont(new Font("黑体", Font.PLAIN, 20));
                    jButton.setBounds(120, 60, 80, 30);
                    jFrame.getContentPane().add(jButton);
                    jFrame.setBounds(((Toolkit.getDefaultToolkit().getScreenSize().width) / 2) - 200,
                            ((Toolkit.getDefaultToolkit().getScreenSize().height) / 2) - 200, 400, 200);
                    jFrame.setVisible(true);
                    FileModel finalFileModel = fileModel;
                    jButton.addActionListener(new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            jFrame.setVisible(false);
                            String text = jTextField.getText();
                            finalFileModel.getFather().getSubMap().remove(finalFileModel.getName());
                            finalFileModel.setName(text);
                            finalFileModel.getFather().getSubMap().put(text, finalFileModel);
                            refreshTree();//刷新文件目录树
                        }
                    });
                    break;
                case "粘贴":
                    if (BufferFileModel != null) {//若用户已粘贴文件
                        if (fileModel.getSubMap().containsKey(BufferFileModel.getName())) {
                            showDialog("存在同名文件或者目录，复制失败！");
                        } else {

                            int startNum = osManager.setFat(BufferFileModel.getSize());
                            FileModel file = new FileModel(BufferFileModel.getName(), BufferFileModel.getType(), startNum, BufferFileModel.getSize());//深拷贝构造文件对象
                            file.setFather(fileModel); //纪录上一层目录
                            file.setFileContent(BufferFileModel.getFileContent());//复制文件内容
                            fileModel.subMap.put(file.getName(), file); //在父目录添加该文件
                            osManager.getTotalFiles().put(file.getName(), file);//放到总文件map里
                            osManager.getFat()[0] -= file.getSize();//磁盘块数更改
                            refreshTree();//刷新文件目录树
                        }
                    }
                    break;
            }
        }
    }

    /**
     * 用于刷新文件目录树
     */
    public void refreshTree() {

        BuildJTree(osManager.getRoot(), root);

        treeModelPointed.reload();


    }

    /**
     * 用于需要确定的提示框的显示
     *
     * @param flag
     */
    public void showConfirmDialog(int flag) {
        switch (flag) {
            case 1:
                if (JOptionPane.showConfirmDialog(null,
                        "存在同名目录，您确定要覆盖吗？", "确认", JOptionPane.YES_NO_OPTION) == 0) {

                }

                break;

        }
    }

    /**
     * 提示框的显示
     *
     * @param str
     */
    public void showDialog(String str) {
        JOptionPane.showMessageDialog(null, str, "提示", JOptionPane.PLAIN_MESSAGE);
    }

    /**
     * 刷新路径的方法
     *
     * @param str
     */
    public void refreshPath(String str) {
        jTextFieldPath.setText(str);
    }

    /**
     * 构造文件目录树的方法
     *
     * @param fileroot
     * @param root
     */
    public void BuildJTree(FileModel fileroot, DefaultMutableTreeNode root) {


        root.removeAllChildren();  //将所有结点移除
        //对根目录进行遍历
        for (String key : fileroot.getSubMap().keySet()) {
            //根据键值获取到当前目录下的文件对象
            FileModel fileModel = fileroot.getSubMap().get(key);
            //如果该文件为非隐藏状态
            if (!fileModel.isAttrIsHide()) {
                //新建结点
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileModel.getName());
                //让根节点add上新的子结点
                root.add(node);
                //递归
                BuildJTree(fileModel, node);
            }

        }
    }

    /**
     * 启动一个进程，不断的刷新按钮的颜色
     */
    public void startUpdateThread() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    JButton jButton = null;

                    //前两个 预先置为使用状态
                    JbuttonList.get(0).setBackground(Color.red);
                    JbuttonList.get(1).setBackground(Color.red);
                    //对fat表进行遍历，若为使用状态，设置颜色为红色，否则设置颜色为绿色
                    for (int i = 2; i < 128; i++) {
                        jButton = JbuttonList.get(i);
                        if (osManager.getFat()[i] == 255) {
                            jButton.setBackground(Color.red);
                        } else {
                            jButton.setBackground(Color.green);
                        }
                    }
                }
            }
        }).start();

    }


    /**
     * 对用户输入的命令进行判断
     *
     * @param str
     */
    public void DoInstructions(String str) {
        String[] strs = osManager.editStr(str);
        switch (strs[0]) {

            case "create":
                JFrame jFrame = new JFrame();
                JTextArea jTextArea = new JTextArea();
                jTextArea.setLineWrap(true);
                jFrame.getContentPane().setLayout(null);
                jFrame.getContentPane().add(jTextArea);
                jTextArea.setBounds(0, 0, 400, 400);
                jFrame.setVisible(true);
                jFrame.addWindowListener(new WindowAdapter() {
                    @Override
                    public void windowClosing(WindowEvent e) {
                        super.windowClosing(e);
                        if (JOptionPane.showConfirmDialog(null,
                                "您确认要保存文件吗", "确认", JOptionPane.YES_NO_OPTION) == 0) {
                            int size = jTextArea.getText().toString().length() / (2 * 64);
                            osManager.createFile(strs[1], strs[2], jTextArea.getText().toString());
                            JOptionPane.showMessageDialog(null, "保存文件成功!", "提示", JOptionPane.PLAIN_MESSAGE);
                        }
                    }
                });
                jFrame.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - 200,
                        ((Toolkit.getDefaultToolkit().getScreenSize().height) / 2) - 200, 400, 400);

                refreshTree();
                break;
            case "makdir":
                if (strs.length < 2) {
                    JOptionPane.showMessageDialog(null, "您所输入的命令有误，请检查！", "提示", JOptionPane.PLAIN_MESSAGE);
                } else {
                    if (osManager.createCatolog(strs[1])) {
                        JOptionPane.showMessageDialog(null, "创建目录成功!", "提示", JOptionPane.PLAIN_MESSAGE);
                    } else {
                        JOptionPane.showMessageDialog(null, "创建目录失败", "提示", JOptionPane.PLAIN_MESSAGE);
                    }

                }
                break;
            case "edit":
                if (strs.length < 2) {
                    showDialog("您所输入的命令有误，请检查！");
                } else {
                    FileModel fileModel = osManager.openFile(strs[1]);
                    //若存在该文件
                    if(fileModel!=null)
                    {
                        showTextFrame(fileModel);
                    }

                }
                break;
            case "open":
                if (strs.length < 2) {
                    System.out.println("您所输入的命令有误，请检查！");
                } else {
                    FileModel fileModel = osManager.openFile(strs[1]);
                    if (fileModel != null) {
                        JFrame jF = new JFrame();
                        JTextArea jT = new JTextArea();
                        jF.getContentPane().add(jT);
                        jF.setVisible(true);

                        int oldSize = fileModel.getSize();
                        jT.setText(fileModel.getFileContent());
                        jF.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent e) {
                                super.windowClosing(e);
                                if (JOptionPane.showConfirmDialog(null,
                                        "您确认要保存文件吗", "确认", JOptionPane.YES_NO_OPTION) == 0) {
                                    int ByteSize = jT.getText().toString().length() / 2;
                                    int size = ByteSize > 64 ? ByteSize % 64 + 1 : 1;
                                    fileModel.setFileContent(jT.getText().toString());
                                    osManager.reAdd(fileModel.getName(), size - oldSize);
                                    JOptionPane.showMessageDialog(null, "保存文件成功!", "提示", JOptionPane.PLAIN_MESSAGE);
                                }
                            }
                        });
                        jF.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - 200,
                                ((Toolkit.getDefaultToolkit().getScreenSize().height) / 2) - 200, 400, 400);

                        refreshTree();

                    }
                }
                break;
            case "cd":
                if (strs.length < 2) {
                    showDialog("请检查您输入的命令！");
                } else {
                    FileModel fileModel = osManager.openFile(strs[1]);
                    if (fileModel != null && (fileModel.getAttr() == 2)) {
                        showTextFrame(fileModel);
                    }
                }
                break;
            case "cd..":
                osManager.backFile();
                osManager.updatePathString();

                break;


            case "chadir": {
                if (strs.length < 2) {
                    System.out.println("您所输入的命令有误，请检查！");
                } else {
                    String[] roadName = strs[1].split("/");

                    osManager.searchFile(roadName);
                    osManager.updatePathString();
                }
                break;
            }
            case "showFAT":
                osManager.showFAT();
                break;


            case "rdir":
                if (strs.length < 2) {
                    showDialog("您所输入的命令有误，请检查！");
                } else {
                    osManager.deleteFile(strs[1]);
                }
                break;
            case "deldir":
                if (strs.length < 2) {
                    showDialog("您所输入的命令有误，请检查！");
                } else {
                    osManager.deleteNotNullCatalog(strs[1]);
                }
                break;
            case "delete":
                if (strs.length < 2) {
                    showDialog("请检查输入的命令!");
                } else {
                    osManager.deleteFile(strs[1]);
                }
                break;

            case "format":
                if (strs.length < 2) {
                    showDialog("请检查输入的命令!");
                } else {
                    if (JOptionPane.showConfirmDialog(null,
                            "您确定要格式化磁盘嘛", "确认", JOptionPane.YES_NO_OPTION) == 0) {
                        osManager.formatRoot();
                        refreshTree();
                        showDialog("格式化已完成！");
                    }


                }
                break;

            case "change"://改变文件属性
                if (strs.length < 3) {
                    showDialog("请检查输入的命令！");
                } else {
                    System.out.println(osManager.getNowCatalog().getName()+" log");
                    osManager.change(strs[1], Integer.parseInt(strs[2]));
                }
                break;
            case "rename":
                if (strs.length < 3) {
                    showDialog("请检查输入的命令！");
                } else {
                    // todo
                    osManager.reName(strs[1], strs[2]);
                }
                break;

            case "type":  //用于显示文件内容
                if (strs.length < 2) {
                    showDialog("请检查输入的命令!");
                } else {
                    FileModel fileModel = osManager.openFile(strs[1]);
                    if (fileModel != null) {
                        JFrame jF = new JFrame();
                        JTextArea jT = new JTextArea();
                        jF.getContentPane().add(jT);
                        jF.setVisible(true);

                        int oldSize = fileModel.getSize();
                        jT.setText(fileModel.getFileContent());
                        jF.addWindowListener(new WindowAdapter() {
                            @Override
                            public void windowClosing(WindowEvent e) {
                                super.windowClosing(e);
                                if (JOptionPane.showConfirmDialog(null,
                                        "您确认要保存文件吗", "确认", JOptionPane.YES_NO_OPTION) == 0) {
                                    int ByteSize = jT.getText().toString().length() / 2;
                                    int size = ByteSize > 64 ? ByteSize % 64 + 1 : 1;
                                    fileModel.setFileContent(jT.getText().toString());
                                    osManager.reAdd(fileModel.getName(), size - oldSize);
                                    JOptionPane.showMessageDialog(null, "保存文件成功!", "提示", JOptionPane.PLAIN_MESSAGE);
                                }
                            }
                        });
                        jF.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - 200,
                                ((Toolkit.getDefaultToolkit().getScreenSize().height) / 2) - 200, 400, 400);

                        refreshTree();
                    }
                }
                break;
        }
    }

    /**
     * 用于显示文件内容，并提供编辑的功能
     *
     * @param fileModel
     */
    private void showTextFrame(FileModel fileModel) {
        if (fileModel != null) {

            //新建窗口，以显示内容
            JFrame jF = new JFrame();
            JTextArea jT = new JTextArea();
            jF.getContentPane().add(jT);
            jF.setVisible(true);

            int oldSize = fileModel.getSize();
            jT.setText(fileModel.getFileContent());
            //判断该文件是否是只读文件
            if (fileModel.isAttrIsOnlyRead()) {
                jT.setEditable(false);
            }
            //为界面的关闭添加监听事件
            jF.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    super.windowClosing(e);
                    if (JOptionPane.showConfirmDialog(null,
                            "您确认要保存文件吗", "确认", JOptionPane.YES_NO_OPTION) == 0) {
                        int ByteSize = jT.getText().toString().length() / 2;
                        int size = ByteSize > 64 ? ByteSize % 64 + 1 : 1;
                        fileModel.setFileContent(jT.getText().toString());
                        osManager.reAdd(fileModel.getName(), size - oldSize);
                        JOptionPane.showMessageDialog(null, "保存文件成功!", "提示", JOptionPane.PLAIN_MESSAGE);
                    }
                }
            });
            jF.setBounds((Toolkit.getDefaultToolkit().getScreenSize().width) / 2 - 200,
                    ((Toolkit.getDefaultToolkit().getScreenSize().height) / 2) - 200, 400, 400);

        }
    }


    public void setOsManager(OSManager osManager) {
        this.osManager = osManager;
    }


}
