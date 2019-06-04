package kr.co.ai.lazylee.webtoonlinkservice;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //=====================================================
    // DB 컨트롤 변수
    DBHelper dbHelper;
    SQLiteDatabase db;
    Cursor cursor;
    //=====================================================



    //=====================================================
    // 내부 알고리즘 변수
    //=====================================================
    int modeNum = 0; // 구동 모드를 컨트롤하는 플래그 변수
    int positionBuff = 0;
    final String[] LIST_MENU = {"네이버", "다음", "레진"};
    // 메인 메뉴
    final String[] WEEK = {"월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일"};
    // 서브 메뉴 리스트
    String bLink; // 선택된 항목의 링크를 기억하는 변수
    //=====================================================

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //=====================================================
        // DB를 사용하기 위한 초기화
        dbHelper = new DBHelper(this);
        db = dbHelper.getWritableDatabase();

        //=====================================================
        // 리스트뷰 선언 및 초기화(초기 어댑터 설정)
        final ListView listview = (ListView)findViewById(R.id.list_view);
        final ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, LIST_MENU);
        listview.setAdapter(adapter);
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            //=====================================================
            // 모드에 따른 리스트뷰 버튼 이벤트 처리
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if ( modeNum == 0){
                    modeNum  = 1;
                    setModeOne(listview, position);
                }
                else if(modeNum == 2){
                    modeNum = 3;
                    setModeTwo(listview, position);
                }
                else if(modeNum == 3){
                    bLink = setModeThree(listview, position);
                    cursor = db.rawQuery("SELECT * FROM NaverLink WHERE _link=\"" + bLink+ "\"", null);
                    cursor.moveToFirst();
                    Log.v("GET_BY_LINK", cursor.getString(1));
                }
            }
        });

        //=====================================================
        // 초기 화면으로 돌려주는 버튼
        Button backBtn = (Button)findViewById(R.id.BackBtn);
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setModeZero(listview, adapter);
            }
        });

        //=====================================================
        // modeThree를 통해 얻은 링크를 웹뷰로 띄워주는 이벤트 처리
        Button btn = (Button)findViewById(R.id.OpenBrowser);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.v("GET_LINK", bLink);
                Intent intent = new Intent(MainActivity.this, SubActivity.class);
                intent.putExtra("link", bLink);
                startActivity(intent);
            }
        });
    }

    //=====================================================
    // 초기화면 구성
    public void setModeZero(ListView listView, ArrayAdapter adapter){
        modeNum = 0;
        listView.setAdapter(adapter);
    }

    //=====================================================
    // 선택된 리스트 항목에 대한 이벤트 수행
    // 해당 사이트에 대한 요일 정보로 리스트 재구성
    // 지금은 네이버 밖에 없으므로 수동으로 처리 이벤트를 작성함
    public void setModeOne(ListView listview, int n){
        positionBuff = n;
        if(listview.getItemAtPosition(n) == "네이버") {
            listview.setAdapter(new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, WEEK));
            modeNum = 2;
        }
        else{
            modeNum = 0;
            Toast.makeText(getApplicationContext(),"해당 데이터가 존재하지 않는것 같아요.\n" + listview.getSelectedItem(), Toast.LENGTH_SHORT).show();
        }
    }

    //=====================================================
    // 선택된 리스트 항목에 대한 이벤트 처리
    // 해당 요일에 대한 웹툰 정보를 불러와서
    // 리스트 뷰를 재구성함
    public void setModeTwo(ListView listview, int n){
        positionBuff = n;
        String sb = "SELECT _name FROM NaverLink WHERE _week=\"" + listview.getItemAtPosition(n) + "\"";
        cursor = db.rawQuery(sb, null);
        cursor.moveToFirst();
        String[] tmp = new String[cursor.getCount()];
        for(int i = 0; i < cursor.getCount(); i++){
            Log.v("LoopTest", String.valueOf(i));
            Log.v("LoopTest", cursor.getString(0));
            tmp[i] = cursor.getString(0);
            cursor.moveToNext();
        }
        listview.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, tmp));
    }

    //=====================================================
    // 선택된 웹툰에 대한 URL링크를 return 해주는 이벤트
    public String setModeThree(ListView listview, int n){
        String sb = "SELECT _link FROM NaverLink WHERE _name=\"" + listview.getItemAtPosition(n) + "\"";
        cursor = db.rawQuery(sb, null);
        cursor.moveToFirst();

        return cursor.getString(0);
    }

}
