package ec.edu.uce.smartmobuce.vista;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.os.SystemClock;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;

import ec.edu.uce.smartmobuce.controlador.ControladorSQLite;
import ec.edu.uce.smartmobuce.controlador.GPSService;
import ec.edu.uce.smartmobuce.controlador.Metodos;
import ec.edu.uce.smartmobuce.R;

import static android.content.ContentValues.TAG;

public class GPSActivity extends AppCompatActivity {
    private TextView textView,textView2;

    private BroadcastReceiver broadcastReceiver;
    private BroadcastReceiver broadcastReceiver1;
    private final Metodos m = new Metodos();

    @Override
    protected void onResume() {
        super.onResume();
        if (broadcastReceiver == null) {

            broadcastReceiver = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    textView.setText("\n" + intent.getExtras().get("coordenadas"));

                }
            };
        }


        registerReceiver(broadcastReceiver, new IntentFilter("location_update"));

        if (broadcastReceiver1 == null) {

            broadcastReceiver1 = new BroadcastReceiver() {
                @Override
                public void onReceive(Context context, Intent intent) {

                    textView2.setText("\n" + intent.getExtras().get("acelerometro"));


                }
            };
        }

        registerReceiver(broadcastReceiver1, new IntentFilter("acelerometro_update"));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (broadcastReceiver != null) {
            unregisterReceiver(broadcastReceiver);
        }
        if (broadcastReceiver1 != null) {
            unregisterReceiver(broadcastReceiver1);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        textView = findViewById(R.id.textView);
        textView2 = findViewById(R.id.textView2);

        //solicita permiso gps
        int permissionCheck= ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
        int permissionCheck1= ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);
        //verifica si los permisos estan otorgados si no solicita el permisos
        if (permissionCheck==-1&&permissionCheck1==-1){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);
            SystemClock.sleep(8000);
            //actualizo el valor del permiso para comparar
            permissionCheck= ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_FINE_LOCATION);
            permissionCheck1= ContextCompat.checkSelfPermission(this,Manifest.permission.ACCESS_COARSE_LOCATION);

        }
        //verifica si se orotgo el permiso y procede a iniciar la aplicacion

        if(permissionCheck==0 && permissionCheck1==0){
            Log.d(TAG, "permiso y ejecuta servicio :");
            Intent i = new Intent(getApplicationContext(), GPSService.class);
            startService(i);
            System.out.println("start service");
            Toast.makeText(this, "inicia servicio", Toast.LENGTH_LONG).show();

        }//caso contrario finaliza la aplicacion
        else
        if(permissionCheck==-1){

            android.os.Process.killProcess(android.os.Process.myPid());
            finish();
        }

        /*
        //verifica el permiso del gps si no esta pide activar e inicia servicio
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,}, 1000);

        } else {
            //ingresa al caso contrario
            Intent i = new Intent(getApplicationContext(), GPSService.class);
            startService(i);
            System.out.println("start service");
            Toast.makeText(this, "inicia servicio", Toast.LENGTH_LONG).show();
            //startService(new Intent(getApplicationContext(), GPSService.class));

        }
      */

    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.refresh:
                ControladorSQLite controller = new ControladorSQLite(this);
                //lista los datos para sincronizar
                ArrayList<HashMap<String, String>> userList = controller.getAllUsers();
                if (userList.size() != 0) {

                }
                //Sync SQLite DB data to remote MySQL DB
                m.syncSQLiteMySQLDB(getApplicationContext());
                return true;
            case R.id.action_settings:

                Toast.makeText(this, "Elaborado por Henry Guamán", Toast.LENGTH_SHORT).show();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }
}





