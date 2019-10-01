package jp.techacademy.satou3.shunsuke.autoslideshowapp2

import android.Manifest
import android.content.ContentUris
import android.content.pm.PackageManager
import android.database.Cursor
import android.os.Build
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*

class MainActivity : AppCompatActivity() {

    private val PERMISSIONS_REQUEST_CODE = 100
    private var cursor: Cursor? = null
    var mTimer: Timer? = null
    var mHandler = Handler()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Android 6.0以降の場合
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // パーミッションの許可状態を確認する
            if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                // 許可されている
                getContentsInfo()
            } else {
                // 許可されていないので許可ダイアログを表示する
                requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), PERMISSIONS_REQUEST_CODE)
            }
            // Android 5系以下の場合
        } else {
            getContentsInfo()
        }


        move_button.setOnClickListener {
            //            　進むボタンでやること
            if (cursor!!.moveToNext()) {
            } else {
                cursor!!.moveToFirst()
            }
            // indexからIDを取得し、そのIDから画像のURIを取得する
            var fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            var id = cursor!!.getLong(fieldIndex)
            var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
            imageView.setImageURI(imageUri)
        }

        back_button.setOnClickListener {
            //             戻るボタンでやること
            if (cursor!!.moveToPrevious()) {
            } else {
                cursor!!.moveToLast()
            }
            // indexからIDを取得し、そのIDから画像のURIを取得する
            var fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            var id = cursor!!.getLong(fieldIndex)
            var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)
                imageView.setImageURI(imageUri)

        }

        start_button.setOnClickListener {


            if (mTimer == null) {
//             再生ボタン1回目でやること
                move_button.isEnabled = false
                back_button.isEnabled = false
                start_button.text = "停止"
                if (mTimer == null){
                    mTimer = Timer()
                    mTimer!!.schedule(object : TimerTask() {
                        override fun run() {
                            mHandler.post {
                                if (cursor!!.moveToNext()) {
                                } else {
                                    cursor!!.moveToFirst()
                                }

                            }
                        }
                    }, 2000, 2000)
                }



            } else {
//             再生ボタン2回目でやること
                move_button.isEnabled = true
                back_button.isEnabled = true
                start_button.text = "再生"
                mTimer = null
            }
        }
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSIONS_REQUEST_CODE ->
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    getContentsInfo()
                }
        }
    }


    private fun getContentsInfo() {
        val resolver = contentResolver
        cursor = resolver.query(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, // データの種類
                null, // 項目(null = 全項目)
                null, // フィルタ条件(null = フィルタなし)
                null, // フィルタ用パラメータ
                null // ソート (null ソートなし)
        )
        if (cursor!!.moveToFirst()) {
            // indexからIDを取得し、そのIDから画像のURIを取得する
            var fieldIndex = cursor!!.getColumnIndex(MediaStore.Images.Media._ID)
            var id = cursor!!.getLong(fieldIndex)
            var imageUri = ContentUris.withAppendedId(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, id)

            imageView.setImageURI(imageUri)
        }

    }

    override fun onDestroy() {
        super.onDestroy()
        cursor!!.close()
    }
}
