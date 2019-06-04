package kr.co.ai.lazylee.webtoonlinkservice;


import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
//=====================================================
// DB 처리 클래스
//=====================================================
public class DBHelper extends SQLiteOpenHelper {
    private String htmlPageUrl = "http://comic.naver.com/webtoon/weekday.nhn";
    private Context context;
    static final String DATABASE_NAME = "NWLink.db";
    static final int DATABASE_VERSION = 4; // <---- 버전 번호로써 숫자가 오를 때 마다 DBHelper의 onUpgrade가 실행된다.
    // 애초에 버전 번호와 관계없이 첫 실행이면 onCreate가 실행된다.
    // 굳이 번호가 1부터 시작안해도 된다. 대신 번호는 무조건 증가값을 가져야함.
    // ex) 1 -> 2 -> 3 -> 17 상관없음 // 1 -> 2 -> 3 -> 17 -> 5 ->7  17에서 갑자기 5로 내려가는 코딩은 안됨
    public DBHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // DB가 존재하지 않을시 DB를 생성함
        db.execSQL("CREATE TABLE NaverLink(_id INTEGER PRIMARY KEY AUTOINCREMENT, _week TEXT, _name TEXT, _link TEXT);");
        db.execSQL("CREATE TABLE CustomLink(_id INTEGER PRIMARY KEY AUTOINCREMENT, _name TEXT, _link TEXT);");
        Toast.makeText(context, "Table 생성 완료", Toast.LENGTH_SHORT).show();

        //=====================================================
        // Jsoup 라는 웹 파싱 모듈 활용
        // AsyncTask와 동일하게 사용가능
        // 웹 -- Jsoup --> 안드로이드 --데이터 파싱--> SQLite
        //=====================================================
        JsoupAsyncTask jsoupAsyncTask = new JsoupAsyncTask();
        jsoupAsyncTask.execute();
    }

    //=====================================================
    // 데이터 삽입 함수
    private void insert(String _week, String _name, String _link){
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put("_week",_week);
        contentValues.put("_name", _name);
        contentValues.put("_link",_link);
        Log.v("DB_TEST", _name);
        db.insert("NaverLink",null, contentValues);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // 버전이 오를때 마다 실행됨
        db.execSQL("DROP TABLE IF EXISTS NaverLink");
        onCreate(db);
    }

    //==============================================================================
    // Html 파서
    //==============================================================================
    private class JsoupAsyncTask extends AsyncTask<Context, Void, Void> {
        protected void onPreExecute() {
            super.onPreExecute();
        }
        @Override
        protected Void doInBackground(Context... contexts) {
            //=====================================================
            // 찾고자하는 데이터의 알맞는 태그, 속성 .. 등을 개발자가 직접 찾아
            // 그에 맞게 알고리즘을 구현해야함
            String _title = null;
            String _link = null;
            try {
                Document doc = Jsoup.connect(htmlPageUrl).get();
                Elements links = doc.select("a[title]");
                // 태그 검색
                Log.v("Elements ", links.get(8).toString());

                for (Element link : links) {
                    if(link.attr("abs:href").contains("weekday=")) {
                        // 속성 검색
                        _title = link.text().trim();
                        _link = link.attr("abs:href");
                        Log.v("Title", link.text().trim());
                        Log.v("Link = ", link.attr("abs:href"));

                        //=====================================================
                        // 요일에 맞게 DB에 넣어줌
                        if(_link.contains("=mon")){
                            insert("월요일", _title, _link);
                        }
                        else if(_link.contains("=tue")){
                            insert("화요일", _title, _link);
                        }
                        else if(_link.contains("=wed")){
                            insert("수요일", _title, _link);
                        }
                        else if(_link.contains("=thu")){
                            insert("목요일", _title, _link);
                        }
                        else if(_link.contains("=fri")){
                            insert("금요일", _title, _link);
                        }
                        else if(_link.contains("=sat")){
                            insert("토요일", _title, _link);
                        }
                        else if(_link.contains("=sun")){
                            insert("일요일", _title, _link);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
