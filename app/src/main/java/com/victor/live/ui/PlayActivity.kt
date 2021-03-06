package com.victor.live

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Message
import android.support.v4.app.ActivityCompat
import android.support.v4.app.ActivityOptionsCompat
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.victor.clips.ui.BaseActivity
import com.victor.hdtv.data.ChannelInfo
import com.victor.kplayer.library.module.Player
import com.victor.live.util.Constant
import com.victor.live.util.MainHandler
import kotlinx.android.synthetic.main.activity_play.*
import java.util.*

class PlayActivity : BaseActivity(),MainHandler.OnMainHandlerImpl {

    var mPlayer: Player? = null
    var channelInfo: ChannelInfo? = null

    override fun handleMainMessage(message: Message) {
        when (message?.what) {
            Player.PLAYER_PREPARING -> {
                mPbLoading.visibility = View.VISIBLE
                mTvSource.visibility = View.VISIBLE
            }
            Player.PLAYER_PREPARED -> {
                mPbLoading.visibility = View.GONE
                mTvSource.visibility = View.GONE
            }
            Player.PLAYER_ERROR -> {
                mPbLoading.visibility = View.VISIBLE
                mTvSource.visibility = View.VISIBLE
                var random = Random()
                var index = random.nextInt(channelInfo?.play_urls!!.size)
                var playUrl = channelInfo?.play_urls!![index].play_url
                mTvSource.setText("播放源：" + playUrl)
                mPlayer?.playUrl(playUrl,true)
            }
            Player.PLAYER_BUFFERING_START -> {
                mPbLoading.visibility = View.VISIBLE
                mTvSource.visibility = View.VISIBLE
            }
            Player.PLAYER_BUFFERING_END -> {
                mPbLoading.visibility = View.GONE
                mTvSource.visibility = View.GONE
            }
            Player.PLAYER_PROGRESS_INFO -> {
            }
            Player.PLAYER_COMPLETE -> {
            }
        }
    }

    companion object {

        fun  intentStart (context: Context, data: ChannelInfo) {
            var intent = Intent(context, PlayActivity::class.java)
            var bundle = Bundle()
            bundle.putSerializable(Constant.INTENT_DATA_KEY,data)
            intent.putExtras(bundle)
            context.startActivity(intent)
        }
    }

    override fun getLayoutResource(): Int {
        return R.layout.activity_play
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initialize()
        initData(intent)
    }

    fun initialize () {
        MainHandler.instance.register(this)
        mPlayer = Player(mSvPlay, MainHandler.instance)
    }

    fun initData (intent: Intent?) {
        channelInfo = intent?.getSerializableExtra(Constant.INTENT_DATA_KEY) as ChannelInfo?
        var playUrl = channelInfo?.play_urls!![0].play_url
        mTvSource.setText("播放源：" + playUrl)
        mPlayer?.playUrl(playUrl,true)
    }
    override fun onResume() {
        super.onResume()
        mPlayer?.resume()
    }

    override fun onPause() {
        super.onPause()
        mPlayer?.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        MainHandler.instance.unregister(this)
        mPlayer?.stop()
        mPlayer = null
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        initData(intent)
    }
}
