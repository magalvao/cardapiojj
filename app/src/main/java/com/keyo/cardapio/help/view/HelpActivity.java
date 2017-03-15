package com.keyo.cardapio.help.view;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.keyo.cardapio.R;
import com.keyo.cardapio.base.BaseActivity;
import com.keyo.cardapio.base.Screens;
import com.keyo.cardapio.help.presenter.HelpPresenter;

/**
 * Created by mgalvao3 on 10/03/17.
 */

public class HelpActivity extends BaseActivity<HelpPresenter> implements HelpView {

    private static final String RECIPIENT_MAIL = "keyo@keyo.me";

    public static Intent createIntent(Context context) {
        return new Intent(context, HelpActivity.class);
    }

    @NonNull
    @Override
    public String getScreenName() {
        return Screens.HELP;
    }

    @NonNull
    @Override
    protected HelpPresenter createPresenter(@NonNull Context context) {
        return new HelpPresenter();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_help);

        findViewById(R.id.sendemail).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts("mailto", 
                                                                               RECIPIENT_MAIL, null));
                intent.putExtra(Intent.EXTRA_SUBJECT, "CardápioJJ - Mensagem");
                startActivity(Intent.createChooser(intent, "Enviar email"));
            }
        });
    }
}
