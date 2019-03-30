# MessageCode
#使用方法

         allprojects {
                           repositories {
                                    ...
                                    maven { url 'https://jitpack.io' }
                           }
                  }
                  
        
        
        dependencies {
	        implementation 'com.github.maweijie2000:MessageCode:1.0.0'
	}
         

#在xml中调用

         <com.mwj.messagecode.messagecode.MessageCodeLayout
                  android:layout_width="wrap_content"
                  android:layout_height="wrap_content"
                 android:focusable="true"
                  android:focusableInTouchMode="true"
                  android:longClickable="false"
                  app:draw_txt_size="14sp"
                 app:interval_width="3dp"
                  app:item_height="50dp"
                  app:item_width="20dp"
                  app:pass_leng="four"
                  app:text_color="@android:color/background_dark" />
         
#
#在代码中使用
#
#//监听事件

        msgcode.setMessageCodeChangeListener(new MessageCodeLayout.MessageCodeChangeListener() {
            @Override
            public void onChange(String pwd) {//改变

            }

            @Override
            public void onNull() {//null

            }

            @Override
            public void onFinished(String code) {//完成

            }
        });
        
#
#
#app:draw_txt_size="14sp"----->字体大小
#app:interval_width="3dp" ---------》每个字符间的距离
#app:item_height="50dp" ----------->每个字符的高度
#app:item_width="20dp"  -------------》每个字符的宽度
#app:pass_leng="four" --------------》长度
#app:text_color="@android:color/background_dark"   --------------》字体颜色
