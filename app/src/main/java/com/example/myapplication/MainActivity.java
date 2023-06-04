package com.example.myapplication;

import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;

import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;


import java.io.IOException;
import java.util.Random;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity implements InputDialog.InputDialogListener {

    String ecg3;
    String ecg2;
    String ecg1;
    double buff;
    double buff2;
    double buff3;
    String IP, Time;
    int start, Sec, Min;

    public int i;
    LineChart mChart;
    private Thread thread;
    private boolean plotData = true;

    Handler handler = new Handler();
    Runnable runnable;
    Runnable time;
    int delay = 40;

    int color[] = {Color.MAGENTA, Color.BLUE, Color.YELLOW, Color.BLACK};

    private TextView IPaddrView;
    private ImageView SetIP;
    private ImageView Start;
    private TextView TimerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        setContentView(R.layout.activity_main);

        IPaddrView = (TextView) findViewById(R.id.IPaddr);
        TimerView = (TextView) findViewById(R.id.Timer);
        SetIP = (ImageView) findViewById(R.id.SetIP);
        Start = (ImageView) findViewById(R.id.Start);

        Start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                start = 1;
            }
        });
        SetIP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openDialog();
            }
        });
        mChart = (LineChart) findViewById(R.id.chats1);

        mChart.getDescription().setEnabled(true);
        mChart.getDescription().setText("Real Time ECG ");
        mChart.setTouchEnabled(false);
        mChart.setDragEnabled(false);
        mChart.setScaleEnabled(false);
        mChart.setDrawGridBackground(false);
        mChart.setPinchZoom(false);
        mChart.setBackgroundColor(Color.WHITE);

        LineData data = new LineData();
        data.setValueTextColor(Color.WHITE);

        mChart.setData(data);

        Legend l = mChart.getLegend();

        l.setForm(Legend.LegendForm.LINE);
        l.setTextColor(Color.BLACK);
        l.setEnabled(false);

        XAxis xl = mChart.getXAxis();
        xl.setTextColor(Color.BLACK);
        xl.setDrawLabels(true);
        xl.setDrawGridLines(false);
        xl.setAvoidFirstLastClipping(true);
        xl.setEnabled(true);

        YAxis leftAxis = mChart.getAxisLeft();
        leftAxis.setTextColor(Color.WHITE);
        leftAxis.setDrawLabels(false);
        leftAxis.setDrawGridLines(true);
        leftAxis.setAxisMaximum(10000f);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setDrawGridLines(true);

        YAxis rightAxis = mChart.getAxisRight();
        rightAxis.setEnabled(false);

        mChart.getAxisLeft().setDrawGridLines(false);
        mChart.getXAxis().setDrawGridLines(false);
        mChart.setDrawBorders(true);
        mChart.setBorderColor(Color.LTGRAY);

    }

    public void timer() {

        if (start == 1) {
            Sec++;
            if (Sec == 60) {
                Min++;
                Sec = 0;
            }
        }
    }

    private void addEntry(int input, int ecg3, int color) {

        LineData data = mChart.getData();
        LineData data2 = mChart.getData();
        LineData data3 = mChart.getData();

        if (data != null) {

            ILineDataSet set = data.getDataSetByIndex(0);
            ILineDataSet set2 = data.getDataSetByIndex(1);
            ILineDataSet set3 = data.getDataSetByIndex(2);

            if (set == null) {
                set = createSet(color);
                data.addDataSet(set);

                set2 = createSet(color);
                data2.addDataSet(set2);

                set3 = createSet(color);
                data3.addDataSet(set3);
            }

            if (input == 1) {
                data.addEntry(new Entry(set.getEntryCount(), ecg3 + 0), 0);
                data.notifyDataChanged();
            }
            if (input == 2) {
                data2.addEntry(new Entry(set2.getEntryCount(), ecg3 + 3000), 1);
                data2.notifyDataChanged();
            }
            if (input == 3) {
                data3.addEntry(new Entry(set3.getEntryCount(), ecg3 + 6000), 2);
                data3.notifyDataChanged();
            }

            mChart.notifyDataSetChanged();
            mChart.setVisibleXRangeMaximum(150);
            mChart.moveViewToX(data.getEntryCount());
        }
        //}
    }

    public void openDialog() {
        InputDialog exampleDialog = new InputDialog();
        exampleDialog.show(getSupportFragmentManager(), "IP Address");
    }


    private LineDataSet createSet(int color) {
        LineDataSet set;
        set = new LineDataSet(null, "Dynamic Data");
        set.setAxisDependency(YAxis.AxisDependency.LEFT);
        set.setLineWidth(0.5f);
        set.setColor(color);
        set.setHighlightEnabled(false);
        set.setDrawValues(false);
        set.setDrawCircles(false);
        set.setMode(LineDataSet.Mode.CUBIC_BEZIER);
        set.setCubicIntensity(0.2f);
        return set;
    }


    private void data(int input, String IP, String src) {
        OkHttpClient client = new OkHttpClient();

        String url = "http://" + IP + src;

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ecg1 = myResponse;
                            buff = Double.parseDouble(ecg1);
                            int d = (int) buff;
                            if (start == 1) {
                                addEntry(input, d, color[1]);
                            }
                        }
                    });
                }
            }
        });

    }

    private void data2(int input, String IP, String src) {
        OkHttpClient client = new OkHttpClient();

        String url = "http://" + IP + src;

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ecg2 = myResponse;
                            buff2 = Double.parseDouble(ecg2);
                            int d = (int) buff2;
                            if (start == 1) {
                                addEntry(input, d, color[1]);
                            }
                        }
                    });
                }
            }
        });

    }

    private void data3(int input, String IP, String src) {
        OkHttpClient client = new OkHttpClient();

        String url = "http://" + IP + src;

        Request request = new Request.Builder()
                .url(url)
                .build();
        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    final String myResponse = response.body().string();

                    MainActivity.this.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            ecg3 = myResponse;
                            buff3 = Double.parseDouble(ecg3);
                            int d = (int) buff3;
                            if (start == 1) {
                                addEntry(input, d, color[1]);
                            }
                        }
                    });
                }
            }
        });

    }

    protected void onResume() {
        handler.postDelayed(time = new Runnable() {
            public void run() {
                handler.postDelayed(time, 1000);
                timer();
                String sec = String.valueOf(Sec);
                String min = String.valueOf(Min);
                if (Sec < 10) {
                    sec = "0" + Sec;
                }
                if (Min < 10) {
                    min = "0" + Min;
                }
                String Time = min + ":" + sec;
                if (Min > 4) {
                    Min = 0;
                    Sec = 0;
                    start = 0;
                }
                TimerView.setText(Time);
            }
        }, 1000);
        handler.postDelayed(runnable = new Runnable() {
            public void run() {
                handler.postDelayed(runnable, delay);
                //if (start == 1){
                data(1, IP, "/ECG1");
                data2(2, IP, "/ECG2");
                data3(3, IP, "/ECG3");
                //}
            }
        }, delay);
        super.onResume();
    }

    protected void onPause() {
        super.onPause();
        handler.removeCallbacks(runnable); //stop handler when activity not visible super.onPause();
    }

    @Override
    public String applyText(String IPaddr) {
        IP = IPaddr;
        String s = "/ECG1";
        String f = "http://" + IPaddr + s;
        IPaddrView.setText(IP);
        return IP;
    }
}