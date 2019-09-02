package beacon;

import async_task.UploadFacturas;
import clases.ClassConfiguracion;

import android.content.Context;
import android.os.CountDownTimer;

public class TimerCountDown extends CountDownTimer{

    public static long BEACON_TIME          = 10000;
    public static long BEACON_TIME_FINISH   = 86400000;

    private Context	            TemporizadorCtx;


    public TimerCountDown(Context _ctx, long _millisInFuture, long _countDownInterval) {
        // TODO Auto-generated constructor stub
        super(_millisInFuture, _countDownInterval);
        this.TemporizadorCtx 	= _ctx;
    }


    @Override
    public void onTick(long millisUntilFinished) {
        new UploadFacturas(this.TemporizadorCtx).execute("");
    }


    @Override
    public void onFinish() {
        // TODO Auto-generated method stub
    }
}
