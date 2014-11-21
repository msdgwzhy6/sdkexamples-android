package com.easemob.chatuidemo;

import android.content.Intent;
import android.content.IntentFilter;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.OnMessageNotifyListener;
import com.easemob.chat.OnNotificationClickListener;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.easemob.chatuidemo.activity.MainActivity;
import com.easemob.chatuidemo.receiver.VoiceCallReceiver;
import com.easemob.chatuidemo.utils.CommonUtils;

public class DemoHXSDKHelper extends HXSDKHelper{

    @Override
    protected void initHXOptions(){
        super.initHXOptions();
        EMChatOptions options = EMChatManager.getInstance().getChatOptions();
        // since demo itself rely on the users in HuanXin IM, so set this to be true
        options.setUseRoster(false);
    }

    @Override
    protected OnMessageNotifyListener getMessageNotifyListener(){
        // 取消注释，app在后台，有新消息来时，状态栏的消息提示换成自己写的
      return new OnMessageNotifyListener() {

          @Override
          public String onNewMessageNotify(EMMessage message) {
              // 设置状态栏的消息提示，可以根据message的类型做相应提示
              String ticker = CommonUtils.getMessageDigest(message, appContext);
              if(message.getType() == Type.TXT)
                  ticker = ticker.replaceAll("\\[.{2,3}\\]", "[表情]");
              return message.getFrom() + ": " + ticker;
          }

          @Override
          public String onLatestMessageNotify(EMMessage message, int fromUsersNum, int messageNum) {
              return null;
             // return fromUsersNum + "个基友，发来了" + messageNum + "条消息";
          }

          @Override
          public String onSetNotificationTitle(EMMessage message) {
              //修改标题,这里使用默认
              return null;
          }

            @Override
            public int onSetSmallIcon(EMMessage message) {
                //设置小图标
                return 0;
            }


      };
    }
    
    @Override
    protected OnNotificationClickListener getNotificationClickListener(){
        return new OnNotificationClickListener() {

            @Override
            public Intent onNotificationClick(EMMessage message) {
                Intent intent = new Intent(appContext, ChatActivity.class);
                ChatType chatType = message.getChatType();
                if (chatType == ChatType.Chat) { // 单聊信息
                    intent.putExtra("userId", message.getFrom());
                    intent.putExtra("chatType", ChatActivity.CHATTYPE_SINGLE);
                } else { // 群聊信息
                            // message.getTo()为群聊id
                    intent.putExtra("groupId", message.getTo());
                    intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                }
                return intent;
            }
        };
    }
    
    @Override
    protected void onConnectionConflict(){
        Intent intent = new Intent(appContext, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra("conflict", true);
        appContext.startActivity(intent);
    }
    
    protected void initListener(){
        super.initListener();
        IntentFilter callFilter = new IntentFilter(EMChatManager.getInstance().getIncomingVoiceCallBroadcastAction());
        appContext.registerReceiver(new VoiceCallReceiver(), callFilter);    
    }
}
