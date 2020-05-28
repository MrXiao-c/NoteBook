# 时间戳
时间戳的编写包括记事本编辑区域的时间展示和记事本列表的时间展示

## 编辑框的时间戳

<img src="https://github.com/MrXiao-c/MyNoteBook/blob/master/screenshots/@JDTPTZ6MAC5KA9P]ZR}~`E.png" width = "350" height = "570" align=center />

### 获取系统时间并转换格式

```
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Log.v("时间：",sdf.format(date));
        tv_now.setText(sdf.format(date));
```

### 把时间保存到数据库，每次修改都更新一次时间

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

### 关于时间的布局

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

### 初始化视图

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

<img src="https://github.com/MrXiao-c/MyNoteBook/blob/master/screenshots/@JDTPTZ6MAC5KA9P]ZR}~`E.png" width = "350" height = "570" align=center />

### 从数据库获取每个笔记的时间
```
//从数据库中读取所有笔记 封装成List<NoteInfo>
    private void getNoteList(){
        noteList.clear();
        Cursor allNotes = Note.getAllNotes(dbHelper);
        for (allNotes.moveToFirst(); !allNotes.isAfterLast(); allNotes.moveToNext()){
            NoteInfo noteInfo = new NoteInfo();
            noteInfo.setId(allNotes.getString(allNotes.getColumnIndex(Note._id)));
            noteInfo.setTitle(allNotes.getString(allNotes.getColumnIndex(Note.title)));
            noteInfo.setContent(allNotes.getString(allNotes.getColumnIndex(Note.content)));
            noteInfo.setDate(allNotes.getString(allNotes.getColumnIndex(Note.time)));
            noteList.add(noteInfo);
        }
    }
```

### 初始化试图

```
public ViewHolder(View itemView) {
        if (itemView == null){
            throw new IllegalArgumentException("item View can not be null!");
        }
        this.itemView = itemView;
        itemIcon = itemView.findViewById(R.id.rand_icon);
        itemNoteTitle = itemView.findViewById(R.id.item_note_title);
        itemNoteDate = itemView.findViewById(R.id.item_note_date);

    }
```
### 时间布局

```
<TextView
            android:id="@+id/item_note_date"
            android:layout_width="match_parent"
            android:layout_height="0dp"
            android:layout_weight="1"
            android:textSize="16sp"
            android:gravity="center_vertical"
            android:singleLine="true"/>
```

# 查询功能

## 通过EditText内容的动态变化来实现查询
<img src="https://github.com/MrXiao-c/MyNoteBook/blob/master/screenshots/@JDTPTZ6MAC5KA9P]ZR}~`E.png" width = "350" height = "570" align=center />

### 通过标题来模糊查询数据库的数据

```
private void getSelectNoteList(String title){
        noteList.clear();
        Cursor allNotes = Note.selectNotes(dbHelper,title);
        for (allNotes.moveToFirst(); !allNotes.isAfterLast(); allNotes.moveToNext()){
            NoteInfo noteInfo = new NoteInfo();
            noteInfo.setId(allNotes.getString(allNotes.getColumnIndex(Note._id)));
            noteInfo.setTitle(allNotes.getString(allNotes.getColumnIndex(Note.title)));
            noteInfo.setContent(allNotes.getString(allNotes.getColumnIndex(Note.content)));
            noteInfo.setDate(allNotes.getString(allNotes.getColumnIndex(Note.time)));
            noteList.add(noteInfo);
        }
    }
```


### 当EditText内容发生变化时会调用此方法，根据内容变化动态来查询数据库的数据，然后把数据装配到适配器中，从而把列表显示出来

```
et_title.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }
            //当EditText内容发生变化时会调用此方法
            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (charSequence.toString().equals("")){
                    search_del.setVisibility(View.GONE);
                }else{
                    search_del.setVisibility(View.VISIBLE);
                }
                String titleChange=et_title.getText().toString();
                Log.v("改变数据：",titleChange);
                getSelectNoteList(titleChange);
                mListAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });
```

<img src="https://github.com/MrXiao-c/MyNoteBook/blob/master/screenshots/%40JDTPTZ6MAC5KA9P%5DZR%7D~%60E.png" width = "200" height = "300" align=center />

## 通过按钮来实现查询

这个方法没有第一种方法人性化，用户体验较差

<img src="https://github.com/MrXiao-c/MyNoteBook/blob/master/screenshots/@JDTPTZ6MAC5KA9P]ZR}~`E.png" width = "350" height = "570" align=center />

### 给查询按钮添加一个监听器

```
private void setListener(){
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this,EditActivity.class);
                startActivity(intent);
            }
        });
        selectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String edit_title=et_title.getText().toString();
                HashMap<Integer,HashMap<String,Object>> map1=new HashMap<Integer,HashMap<String,Object>>();;
                map1=Note.getNote(dbHelper,edit_title);
                Log.v("查询",map1.toString());
                Intent intent = new Intent(MainActivity.this,SelectActivity.class);
                intent.putExtra("edit_title",edit_title );
                startActivity(intent);
                //getSelectNoteList(edit_title);
            }
        });
```



## 清空文本框

<img src="https://github.com/MrXiao-c/MyNoteBook/blob/master/screenshots/@JDTPTZ6MAC5KA9P]ZR}~`E.png" width = "350" height = "570" align=center />

### 把EditTtext的内容置为空

```
search_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNoteList();;//初始化数据
                et_title.setText("");
                mListAdapter.notifyDataSetChanged();//更新RecycleView
            }
        });
```
