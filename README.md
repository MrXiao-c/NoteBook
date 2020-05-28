# 时间戳
时间戳的编写包括记事本编辑区域的时间展示和记事本列表的时间展示

<img src="https://github.com/MrXiao-c/MyNoteBook/blob/master/screenshots/%40JDTPTZ6MAC5KA9P%5DZR%7D~%60E.png" width = "200" height = "300" align=center />
<img src="https://github.com/MrXiao-c/MyNoteBook/blob/master/screenshots/%40JDTPTZ6MAC5KA9P%5DZR%7D~%60E.png" width = "200" height = "300" align=center />
## 编辑框的时间戳
获取系统时间并转换格式

```
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.v("时间：",sdf.format(date));
        tv_now.setText(sdf.format(date));
```

把时间保存到数据库

```
private void saveNote(){
        NoteDataBaseHelper dbHelper = MainActivity.getDbHelper();

        ContentValues values = new ContentValues();
        values.put(Note.title,et_title.getText().toString());
        values.put(Note.content,et_content.getText().toString());
        values.put(Note.time,tv_now.getText().toString());
        if (insertFlag){
            Note.insertNote(dbHelper,values);
        }else{
            Note.updateNote(dbHelper,Integer.parseInt(currentNote.getId()),values);
        }
    }
    
```

关于时间的布局

```
<TextView
        android:id="@+id/tv_now"
        android:layout_width="match_parent"
        android:layout_height="30dp"
        android:background="#A5BECE"
        android:textAlignment="textEnd"
        android:gravity="end|center_vertical"
        android:paddingRight="8dp"/>
```

初始化视图

```
private void initView(){
        btn_save = findViewById(R.id.btn_save);
        btn_return = findViewById(R.id.btn_return);
        tv_now = findViewById(R.id.tv_now);
        et_content = findViewById(R.id.edit_content);
        et_title = findViewById(R.id.edit_title);
        //以下为测试代码
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.v("时间：",sdf.format(date));
        tv_now.setText(sdf.format(date));

    }
```

## 记事本列表的时间戳

# 查询功能 
<img src="https://github.com/MrXiao-c/MyNoteBook/blob/master/screenshots/%40JDTPTZ6MAC5KA9P%5DZR%7D~%60E.png" width = "200" height = "300" align=center />


**加重强调**  (示例：粗体)  
 __加重强调__ (示例：粗体)  
***特别强调*** (示例：粗斜体)  
___特别强调___  (示例：粗斜体)  
2、代码  
`<hello world>`  
3、代码块高亮  
```
@Override
protected void onDestroy() {
    EventBus.getDefault().unregister(this);
    super.onDestroy();
}
```  
4、表格 （建议在表格前空一行，否则可能影响表格无法显示）
 
 表头  | 表头  | 表头
 ---- | ----- | ------  
 单元格内容  | 单元格内容 | 单元格内容 
 单元格内容  | 单元格内容 | 单元格内容  
 
5、其他引用
图片  
![图片名称](https://www.baidu.com/img/bd_logo1.png)  
链接  
[链接名称](https://www.baidu.com/)    
6、列表 
1. 项目1  
2. 项目2  
3. 项目3  
   * 项目1 （一个*号会显示为一个黑点，注意⚠️有空格，否则直接显示为*项目1） 
   * 项目2   
 
7、换行（建议直接在前一行后面补两个空格）
直接回车不能换行，  
可以在上一行文本后面补两个空格，  
这样下一行的文本就换行了。
或者就是在两行文本直接加一个空行。
也能实现换行效果，不过这个行间距有点大。  
 
8、引用
> 第一行引用文字  
> 第二行引用文字   



