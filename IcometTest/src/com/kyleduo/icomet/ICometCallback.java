package com.kyleduo.icomet;

import com.kyleduo.icomet.message.Message;

public interface ICometCallback {

	// a message with data arrived
//	public void onDataMsgArrived(Message.Content content);
	public void onDataMsgArrived(String content);

	// a message arrived, maybe not with data
	public void onMsgArrived(Message msg);

	// a error message arrived
	public void onErrorMsgArrived(Message msg);
	
	// message format error, can not parse json
	public void onMsgFormatError();

}
