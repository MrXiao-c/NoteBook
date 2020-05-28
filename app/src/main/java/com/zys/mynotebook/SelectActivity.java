package com.zys.mynotebook;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class SelectActivity extends AppCompatActivity {
        private ListView noteListView;
        private Button addBtn;
        private Button selectBtn;
        private Button btn_return;
        private EditText et_title;

        private List<NoteInfo> noteList = new ArrayList<>();
        private ListAdapter mListAdapter;

        private static NoteDataBaseHelper dbHelper;
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.select_layout);
            dbHelper = new NoteDataBaseHelper(this,"MyNote.db",null,1);
            //先测试查询一条数据
        /*HashMap<String, Object> NoteMapTest = new HashMap<String, Object>();
        NoteMapTest=Note.getNote(dbHelper,"你好");
        Log.v("查询",NoteMapTest.toString());*/
            initView();
            setListener();
            //跳转回主界面 刷新列表
            Intent intent = getIntent();
            String msg=intent.getStringExtra("edit_title");
            if (intent != null){
                getNoteList(msg);
                mListAdapter.refreshDataSet();
            }
        }
        //初始化视图
        private void initView(){
            noteListView = findViewById(R.id.note_list);
            addBtn = findViewById(R.id.btn_add);
            //selectBtn = findViewById(R.id.btn_select);
            btn_return = findViewById(R.id.btn_return);
            //获取noteList
            Intent intent = getIntent();
            String msg=intent.getStringExtra("edit_title");
            getNoteList(msg);
            mListAdapter = new ListAdapter(SelectActivity.this,noteList);
            noteListView.setAdapter(mListAdapter);
        }
        //设置监听器
        private void setListener(){
            btn_return.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onBackPressed();
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
                    intent.setClass(SelectActivity.this, EditActivity.class);
                    startActivity(intent);
                }
            });

            noteListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {
                    final NoteInfo noteInfo = noteList.get(position);
                    String title = "警告";
                    new AlertDialog.Builder(SelectActivity.this)
                            .setIcon(R.drawable.note)
                            .setTitle(title)
                            .setMessage("确定要删除吗?")
                            .setPositiveButton(R.string.btn_confirm, new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Note.deleteNote(dbHelper,Integer.parseInt(noteInfo.getId()));
                                    noteList.remove(position);
                                    mListAdapter.refreshDataSet();
                                    Toast.makeText(SelectActivity.this,"删除成功！",Toast.LENGTH_LONG).show();
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
        private void getNoteList(String title){
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
            Log.v("筛选：",noteList.toString());
        }
        //重写返回按钮处理事件
        @Override
        public void onBackPressed() {
            Intent intent = new Intent(SelectActivity.this,MainActivity.class);
            startActivity(intent);
        }
        //给其他类提供dbHelper
        public static NoteDataBaseHelper getDbHelper() {
            return dbHelper;
        }
}

