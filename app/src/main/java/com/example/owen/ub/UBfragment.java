package com.example.owen.ub;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.jsoup.Jsoup;
import org.jsoup.safety.Whitelist;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A placeholder fragment containing a simple view.
 */
public class UBfragment extends Fragment {

    private ArrayAdapter<String> mUBtitleAdapter;
    private ArrayAdapter<String> mUBinforAdapter;
    List<Map<String, String>> listitem = new ArrayList<>();
    private SimpleAdapter  mZhihuAdapter;
    ArrayList<String> description = new ArrayList<String>();

    public UBfragment() {
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.action_refresh:
                getUBnews getUBnews = new getUBnews();
                getUBnews.execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        String[] testString = {
//                "Family Welcome events need volunteers",
//                "Writing Centre to hold Open House event",
//                "Remembering Donna Wilhelm and Anupam Banerji"
//        };
//        ArrayList<String> testList = new ArrayList<String>(Arrays.asList(testString));

//         Initialize the adapter

        mZhihuAdapter = new SimpleAdapter(getActivity(),
                listitem,
                R.layout.list_item_textview,
                new String[]{"ItemTitle","ItemInfo"},
                new int[]{R.id.list_title_textview,R.id.list_intro_textview});

        mUBtitleAdapter = new ArrayAdapter<String>(getActivity(),
                R.layout.list_item_textview,R.id.list_title_textview,new ArrayList<String>()
                );
//        mUBinforAdapter = new ArrayAdapter<String>(getActivity(),
//                R.layout.list_item_textview,R.id.list_intro_textview,new ArrayList<String>());

//        update the adapter
        getUBnews zhihu = new getUBnews();
        zhihu.execute();

        View RootView = inflater.inflate(R.layout.fragment_main, container, false);

        ListView listView = (ListView) RootView.findViewById(R.id.ub_listview);

        listView.setAdapter(mZhihuAdapter);


        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {

                String contentHtml = description.get(position);
                Intent intent = new Intent(getActivity(), DetailActivity.class)
                        .putExtra(Intent.EXTRA_TEXT, contentHtml);
                startActivity(intent);
            }
        });

        return RootView;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    public class getUBnews extends AsyncTask<Void,Void,Void>{
        public getUBnews() {
            super();
        }
        private final String LOG_TAG = getUBnews.class.getSimpleName();

        ArrayList<String> title = new ArrayList<String>();
        ArrayList<String> link = new ArrayList<String>();


        protected Void doInBackground(Void... params) {

            HttpURLConnection urlConnection = null;

            final String TITLE = "title";
            final String DESC = "description";
            final String LINK = "link";
            final String LANG = "language";
            final String AUTHOR = "author";


            try {
                URL url = new URL("http://www.zhihu.com/rss");
                XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
                factory.setNamespaceAware(false);
                XmlPullParser xpp = factory.newPullParser();

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();

                xpp.setInput(inputStream, "utf-8");

                int eventType = xpp.getEventType();
                boolean insideitem = false;

                while(eventType != XmlPullParser.END_DOCUMENT){
                    if(eventType == XmlPullParser.START_TAG){
                        if(xpp.getName().equals("item")){
                            insideitem = true;
                        }
                        else if(xpp.getName().equals(TITLE)){
                            if(insideitem){
                                title.add(xpp.nextText());
                            }
                        }else if(xpp.getName().equals(LINK)){
                            if(insideitem){
                                link.add(xpp.nextText());
                            }
                        }else if(xpp.getName().equals(DESC)){
                            if(insideitem){
                                description.add(xpp.nextText());
                            }
                        }
//                        Log.v(LOG_TAG,"Attribute: " +  xpp.getAttributeName(0));
                    }else if(eventType == XmlPullParser.END_TAG && xpp.getName().equals("item")){
//                        Log.v(LOG_TAG,"END TAG encountered!!");
                        insideitem = false;
                    }
                    eventType = xpp.next();

                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
                Log.e(LOG_TAG,"URL access failuere");
            } catch (XmlPullParserException e) {
                e.printStackTrace();

            } catch (ProtocolException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            urlConnection.disconnect();

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {

            listitem.clear();
            for(int i = 0; i < title.size();i++){
                HashMap<String, String> map = new HashMap<>();
                map.put("ItemTitle",title.get(i));

                String html = description.get(i);
                String info = Jsoup.clean(html, Whitelist.none()).substring(0,40);
//                Log.v(LOG_TAG,info);
                map.put("ItemInfo",info);
                listitem.add(map);
            }
            mZhihuAdapter.notifyDataSetChanged();






            if(!mUBtitleAdapter.isEmpty()){
                mUBtitleAdapter.clear();
            }
            for(String headline : title){
                mUBtitleAdapter.add(headline);
            }

//            if(!mUBinforAdapter.isEmpty()){
//                mUBinforAdapter.clear();
//            }
//            for(String html : description){
//                Document doc = Jsoup.parse(html);
//                String info = Jsoup.clean(html, Whitelist.none()).substring(0,40);
//                mUBinforAdapter.add(info);
//            }
        }
    }
}
