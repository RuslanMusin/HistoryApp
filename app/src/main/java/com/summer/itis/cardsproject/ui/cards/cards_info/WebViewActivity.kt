package com.summer.itis.cardsproject.ui.cards.cards_info

import QuestionFragment.Companion.CARD_JSON
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.webkit.WebResourceRequest
import android.webkit.WebSettings.PluginState
import android.webkit.WebView
import android.webkit.WebViewClient
import com.summer.itis.cardsproject.R
import com.summer.itis.cardsproject.R.string.card
import com.summer.itis.cardsproject.model.AbstractCard
import com.summer.itis.cardsproject.ui.base.BaseActivity
import com.summer.itis.cardsproject.utils.Const.gsonConverter
import kotlinx.android.synthetic.main.activity_wiki.*


/**
 * Created by Home on 14.07.2018.
 */
class WebViewActivity : BaseActivity() {

    lateinit var card: AbstractCard

    companion object {

        fun start(context: Context,card: AbstractCard){
            val intent = Intent(context, WebViewActivity::class.java)
            intent.putExtra(CARD_JSON,gsonConverter.toJson(card))
            context.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_wiki)

        card = gsonConverter.fromJson(intent.getStringExtra(CARD_JSON),AbstractCard::class.java)
        toolbar.title = card.name
        setSupportActionBar(toolbar)
        setBackArrow(toolbar)

        webView.getSettings().setJavaScriptEnabled(true)
        webView.loadUrl(card.wikiUrl)

          webView.webViewClient = object : WebViewClient() {
              override fun shouldOverrideUrlLoading(view: WebView?, url: String): Boolean {
                  view?.loadUrl(url)
                  return true
              }
          }
         /* webView.getSettings().setJavaScriptEnabled(true)
          webView.getSettings().setPluginState(PluginState.ON)
          webView.loadUrl(intent.getStringExtra("URL"))
          setContentView(webView)*/

    }

    override fun onBackPressed() {
        if(webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}