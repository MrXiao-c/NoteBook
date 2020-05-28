package com.zys.mynotebook;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView noteListView;
    private Button addBtn;
    private Button selectBtn;
    private ImageView search_del;
    private EditText et_title;


    private List<NoteInfo> noteList = new ArrayList<>();
    private ListAdapter mListAdapter;

    private static NoteDataBaseHelper dbHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dbHelper = new NoteDataBaseHelper(this,"MyNote.db",null,1);
        //先测试添加一条数据
        /*ContentValues values = new ContentValues();
        values.put(Note.title,"测试笔记");
        values.put(Note.content,"以下为测试内容！！！");
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        values.put(Note.time,sdf.format(date));
        Note.insertNote(dbHelper,values);*/
        /*HashMap<String, Object> NoteMapTest = new HashMap<String, Object>();
        NoteMapTest=Note.getNote(dbHelper,"你好");
        Log.v("查询",NoteMapTest.toString());*/
        initView();
        setListener();
        //跳转回主界面 刷新列表
        search_del=(ImageView)findViewById(R.id.search_delete);//实例化查找删除按钮
        search_del.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getNoteList();;//初始化数据
                et_title.setText("");
                mListAdapter.notifyDataSetChanged();//更新RecycleView
            }
        });
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
        Intent intent = getIntent();
        if (intent != null){
            getNoteList();
            mListAdapter.refreshDataSet();
        }
    }
    //初始化视图
    private void initView(){
        noteListView = findViewById(R.id.note_list);
        addBtn = findViewById(R.id.btn_add);
        selectBtn = findViewById(R.id.btn_select);
        et_title =(EditText)findViewById(R.id.edit_title);
        //获取noteList
        getNoteList();
        mListAdapter = new ListAdapter(MainActivity.this,noteList);
        noteListView.setAdapter(mListAdapter);
    }
    //设置监听器
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

        noteListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                NoteInfo noteInfo = noteList.get(position);
                Intent intent = new Intent();
                Bundle bundle = new Bundle();
                bundle.putSerializable("noteInfo",(Serializable)noteInfo);
                intent.putExtras(bundle);
                intent.setClass(MainActivity.this, EditActivity.class);
                startActivity(intent);
            }
        });

        noteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                final NoteInfo noteInfo = noteList.get(position);
                String title = "警告";
                new AlertDialog.Builder(MainActivity.this)
                        .setIcon(R.drawable.note)
                        .setTitle(title)
                        .setMessage("确定要删除吗?")
                        .setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Note.deleteNote(dbHelper,Integer.parseInt(noteInfo.getId()));
                                noteList.remove(position);
                                mListAdapter.refreshDataSet();
                                Toast.makeText(MainActivity.this,"删除成功！",Toast.LENGTH_LONG).show();
                            }
                        })
                        .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        }).create().show();
                return true;
            }
        });
    }
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

    //重写返回按钮处理事件
    @Override
    public void onBackPressed() {
        String title = "提示";
        new AlertDialog.Builder(MainActivity.this)
                .setIcon(R.drawable.note)
                .setTitle(title)
                .setMessage("确定要退出吗?")
                .setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                })
                .setNegativeButton(R.string.btn_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                }).create().show();
    }
    //给其他类提供dbHelper
    public static NoteDataBaseHelper getDbHelper() {
        return dbHelper;
    }
}
