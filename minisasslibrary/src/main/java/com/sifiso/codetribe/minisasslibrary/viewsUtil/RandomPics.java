package com.sifiso.codetribe.minisasslibrary.viewsUtil;

import android.content.Context;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.sifiso.codetribe.minisasslibrary.R;

import java.util.Random;
import java.util.Timer;

/**
 * Created by sasa. 2015-02-20
 */
public class RandomPics {

    static Timer timer;

    public interface RandomPicsListener {
        public void onCompleteFlash();
    }

    static RandomPicsListener mListener;
    static int count = 0;

    public static void getImage(Context ctx, ImageView v, TextView txt, RandomPicsListener listener) {
        mListener = listener;
        Random random = new Random(System.currentTimeMillis());
        Log.i("Random", "starting switch");
        int index = random.nextInt(14);
        try {
            switch (index) {

                case 0:
                    v.setImageDrawable(ctx.getResources().getDrawable(R.drawable.bergriver));

                    txt.setText("Berg River");
                    break;
                case 1:
                    v.setImageDrawable(ctx.getResources().getDrawable(R.drawable.blyderiver));
                    txt.setText("Blyde River");
                    break;
                case 2:
                    v.setImageDrawable(ctx.getResources().getDrawable(R.drawable.breerderiver));
                    txt.setText("Breerde River");
                    break;
                case 3:
                    v.setImageDrawable(ctx.getResources().getDrawable(R.drawable.crocodileriver));
                    txt.setText("Crocodile River");
                    break;
                case 4:
                    v.setImageDrawable(ctx.getResources().getDrawable(R.drawable.gamtoosriver));
                    txt.setText("Gamtoos River");
                    break;
                case 5:
                    v.setImageDrawable(ctx.getResources().getDrawable(R.drawable.greatfishriver));
                    txt.setText("GreatFish River");
                    break;
                case 6:
                    v.setImageDrawable(ctx.getResources().getDrawable(R.drawable.greatkeiriver));
                    txt.setText("GreatKoi River");
                    break;
                case 7:
                    v.setImageDrawable(ctx.getResources().getDrawable(R.drawable.komatiriver));
                    txt.setText("Komati River");
                    break;
                case 8:
                    v.setImageDrawable(ctx.getResources().getDrawable(R.drawable.limpoporiver));
                    txt.setText("Limpopo River");
                    break;
                case 9:
                    v.setImageDrawable(ctx.getResources().getDrawable(R.drawable.olifantsriver));
                    txt.setText("Olifants River");
                    break;
                case 10:
                    v.setImageDrawable(ctx.getResources().getDrawable(R.drawable.orangeriver));
                    txt.setText("Orange River");
                    break;
                case 11:
                    v.setImageDrawable(ctx.getResources().getDrawable(R.drawable.tungelariver));
                    txt.setText("Tungela River");
                    break;
                case 12:
                    v.setImageDrawable(ctx.getResources().getDrawable(R.drawable.umfoloziriver));
                    txt.setText("Umfolozi River");
                    break;
                case 13:
                    v.setImageDrawable(ctx.getResources().getDrawable(R.drawable.umgeniriver));
                    txt.setText("Umgeni River");
                    break;
                case 14:

                    v.setImageDrawable(ctx.getResources().getDrawable(R.drawable.vaalriver));
                    txt.setText("Vaal River");
                    break;
                default:
                    break;
            }
            count++;
            if (count > 10) {
                count = 0;
                listener.onCompleteFlash();
            }
        } catch (Exception e) {
            Log.d("RandomPics", e.toString());
        }
    }

    public static void getResources() {
        return;
    }
}
