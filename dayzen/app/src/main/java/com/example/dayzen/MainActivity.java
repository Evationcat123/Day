package com.example.dayzen;

import android.app.*;import android.os.*;import android.content.*;import android.graphics.*;import android.graphics.drawable.*;import android.view.*;import android.view.inputmethod.InputMethodManager;import android.widget.*;import java.text.*;import java.util.*;

public class MainActivity extends Activity {
  DayView dayView; ArrayList<Task> tasks=new ArrayList<>(); android.content.SharedPreferences prefs;
  int[] palette={0xFF7C83F5,0xFFFF9F68,0xFF63C7A8,0xFFE879A9,0xFFF2C94C,0xFF7EB6F6,0xFFA78BFA};
  @Override public void onCreate(Bundle b){super.onCreate(b); getWindow().setStatusBarColor(Color.rgb(247,247,244));getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);prefs=getSharedPreferences("dayzen",0); load(); build();}
  void build(){
    LinearLayout root=new LinearLayout(this);root.setOrientation(LinearLayout.VERTICAL);root.setPadding(dp(18),dp(10),dp(18),dp(8));root.setBackgroundColor(0xFFF7F7F4);
    LinearLayout top=new LinearLayout(this);top.setGravity(Gravity.CENTER_VERTICAL);TextView title=t("DayZen",28,0xFF171717);top.addView(title,new LinearLayout.LayoutParams(0,dp(52),1));
    TextView add=t("+",30,0xFF171717);add.setGravity(Gravity.CENTER);GradientDrawable circle=new GradientDrawable();circle.setColor(0xFFFFFFFF);circle.setShape(GradientDrawable.OVAL);add.setBackground(circle);add.setOnClickListener(v->showTaskDialog(null));top.addView(add,new LinearLayout.LayoutParams(dp(48),dp(48)));root.addView(top);
    LinearLayout nav=new LinearLayout(this);nav.setGravity(Gravity.CENTER_VERTICAL);TextView prev=t("‹",30,0xFF444444), date=t(new SimpleDateFormat("EEE, d MMM",Locale.getDefault()).format(new Date()),16,0xFF333333), next=t("›",30,0xFF444444);date.setGravity(Gravity.CENTER);nav.addView(prev,new LinearLayout.LayoutParams(dp(48),dp(42)));nav.addView(date,new LinearLayout.LayoutParams(0,dp(42),1));nav.addView(next,new LinearLayout.LayoutParams(dp(48),dp(42)));root.addView(nav);root.addView(space(4));
    dayView=new DayView(this);root.addView(dayView,new LinearLayout.LayoutParams(-1,0,1));
    TextView hint=t("Tap  +  to plan your day  •  Tap a block to edit",13,0xFF777777);hint.setGravity(Gravity.CENTER);root.addView(hint,new LinearLayout.LayoutParams(-1,dp(40)));
    setContentView(root); add.setContentDescription("Add task");
    prev.setOnClickListener(v->{dayView.offset--;dayView.invalidate();});next.setOnClickListener(v->{dayView.offset++;dayView.invalidate();});
  }
  TextView t(String s,float z,int c){TextView v=new TextView(this);v.setText(s);v.setTextSize(z);v.setTextColor(c);return v;} View space(int h){Space s=new Space(this);s.setLayoutParams(new LinearLayout.LayoutParams(1,dp(h)));return s;} int dp(int x){return (int)(x*getResources().getDisplayMetrics().density+.5f);}
  void showTaskDialog(Task edit){
    LinearLayout box=new LinearLayout(this);box.setOrientation(LinearLayout.VERTICAL);box.setPadding(dp(24),dp(4),dp(24),0); EditText name=new EditText(this);name.setHint("What are you doing?");name.setSingleLine(true);box.addView(name);
    LinearLayout row=new LinearLayout(this);row.setGravity(Gravity.CENTER_VERTICAL); EditText start=new EditText(this);start.setHint("Start 09:00");start.setInputType(2);EditText dur=new EditText(this);dur.setHint("Minutes");dur.setInputType(2);row.addView(start,new LinearLayout.LayoutParams(0,dp(55),1));row.addView(dur,new LinearLayout.LayoutParams(0,dp(55),1));box.addView(row);
    if(edit!=null){name.setText(edit.name);start.setText(String.format(Locale.US,"%02d:%02d",edit.start/60,edit.start%60));dur.setText(""+edit.duration);}
    AlertDialog d=new AlertDialog.Builder(this).setTitle(edit==null?"Add a block":"Edit block").setView(box).setNegativeButton("Cancel",null).setPositiveButton(edit==null?"Add":"Save",null).create();
    d.setOnShowListener(x->d.getButton(-1).setOnClickListener(v->{String n=name.getText().toString().trim();int st=parseTime(start.getText().toString());int du=parseInt(dur.getText().toString(),30);if(n.isEmpty())n="Focus";if(edit==null)tasks.add(new Task(n,st,du,palette[tasks.size()%palette.length]));else{edit.name=n;edit.start=st;edit.duration=du;}save();dayView.invalidate();d.dismiss();}));d.show();
  }
  int parseTime(String s){try{String[] a=s.trim().split(":");return Math.max(0,Math.min(1439,Integer.parseInt(a[0])*60+Integer.parseInt(a[1])));}catch(Exception e){return 540;}}int parseInt(String s,int def){try{return Math.max(5,Math.min(720,Integer.parseInt(s)));}catch(Exception e){return def;}}
  void save(){StringBuilder b=new StringBuilder();for(Task x:tasks)b.append(x.name.replace("|"," ")).append('|').append(x.start).append('|').append(x.duration).append('|').append(x.color).append('\n');prefs.edit().putString("tasks",b.toString()).apply();}
  void load(){String raw=prefs.getString("tasks","");for(String l:raw.split("\\n")){try{String[] a=l.split("\\|",-1);if(a.length>=4)tasks.add(new Task(a[0],Integer.parseInt(a[1]),Integer.parseInt(a[2]),Integer.parseInt(a[3])));}catch(Exception ignored){}}if(tasks.isEmpty()){tasks.add(new Task("Sleep",0,420,0xFF8C8EDB));tasks.add(new Task("Morning",420,60,0xFFF2C94C));tasks.add(new Task("School / Work",510,420,0xFF7C83F5));tasks.add(new Task("Break",930,45,0xFF63C7A8));tasks.add(new Task("Exercise",1080,60,0xFFFF9F68));save();}}
  class Task{String name;int start,duration,color;Task(String n,int s,int d,int c){name=n;start=s;duration=d;color=c;}}
  class DayView extends View{
    Paint p=new Paint(3);int offset=0;float downX,downY;
    DayView(Context c){super(c);p.setTypeface(Typeface.create("sans",Typeface.NORMAL));setLayerType(View.LAYER_TYPE_SOFTWARE,null);}
    protected void onDraw(Canvas c){super.onDraw(c);float w=getWidth(),h=getHeight();float cx=w/2,cy=h/2;float r=Math.min(w,h)*.38f; p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(dp(28));p.setColor(0xFFE7E7E3);c.drawCircle(cx,cy,r,p);
      // blocks
      for(Task x:tasks){float a1=(x.start/1440f)*360f-90f;float sweep=Math.min(359f,x.duration/1440f*360f);p.setStyle(Paint.Style.STROKE);p.setStrokeWidth(dp(25));p.setStrokeCap(Paint.Cap.ROUND);p.setColor(x.color);c.drawArc(cx-r,cy-r,cx+r,cy+r,a1,sweep,false,p);}
      p.setStrokeCap(Paint.Cap.BUTT);p.setStyle(Paint.Style.FILL);p.setColor(0xFF242424);c.drawCircle(cx,cy,dp(5),p);
      Calendar now=Calendar.getInstance();float minute=now.get(Calendar.HOUR_OF_DAY)*60+now.get(Calendar.MINUTE);float ang=(float)(minute/1440f*2*Math.PI-Math.PI/2);p.setColor(0xFF202020);p.setStrokeWidth(dp(2));c.drawLine(cx,cy,cx+(float)Math.cos(ang)*(r+dp(16)),cy+(float)Math.sin(ang)*(r+dp(16)),p);p.setColor(0xFF5B5CE2);c.drawCircle(cx,cy,dp(4),p);
      p.setTextAlign(Paint.Align.CENTER);p.setTypeface(Typeface.DEFAULT_BOLD);p.setTextSize(dp(22));p.setColor(0xFF1B1B1B);c.drawText(new SimpleDateFormat("HH:mm",Locale.getDefault()).format(new Date()),cx,cy+dp(8),p);p.setTypeface(Typeface.DEFAULT);p.setTextSize(dp(12));p.setColor(0xFF777777);c.drawText("TODAY",cx,cy+dp(28),p);
      // hour labels
      p.setTextSize(dp(11));p.setColor(0xFF8A8A8A);for(int hr=0;hr<24;hr+=3){double a=hr/24.0*2*Math.PI-Math.PI/2;float tx=cx+(float)Math.cos(a)*(r+dp(45)),ty=cy+(float)Math.sin(a)*(r+dp(45))+dp(4);c.drawText(String.format(Locale.US,"%02d",hr),tx,ty,p);}
      // task labels on ring
      p.setTextSize(dp(11));for(Task x:tasks){float mid=(x.start+x.duration/2)/1440f*2*(float)Math.PI-(float)Math.PI/2;float tx=cx+(float)Math.cos(mid)*(r-dp(48)),ty=cy+(float)Math.sin(mid)*(r-dp(48));String label=x.name.length()>12?x.name.substring(0,12)+"…":x.name;p.setColor(0xFF303030);c.drawText(label,tx,ty,p);}
    }
    public boolean onTouchEvent(android.view.MotionEvent e){if(e.getAction()==0){downX=e.getX();downY=e.getY();return true;}if(e.getAction()==1){float dx=e.getX()-downX,dy=e.getY()-downY;float cx=getWidth()/2,cy=getHeight()/2;float rr=(float)Math.hypot(e.getX()-cx,e.getY()-cy);float r=Math.min(getWidth(),getHeight())*.38f;if(Math.abs(dx)<dp(12)&&Math.abs(dy)<dp(12)&&rr>r-dp(45)&&rr<r+dp(45)){double ang=Math.atan2(e.getY()-cy,e.getX()-cx)+Math.PI/2;if(ang<0)ang+=2*Math.PI;int min=(int)(ang/(2*Math.PI)*1440);for(Task t:tasks){if(min>=t.start&&min<=t.start+t.duration){showTaskDialog(t);return true;}}showTaskDialog(null);}return true;}return true;}
  }
}
