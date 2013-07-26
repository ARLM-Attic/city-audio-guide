package com.gerken.audioGuide.services;

import java.io.InputStream;

import javax.crypto.Cipher;
import javax.crypto.CipherInputStream;
import javax.crypto.spec.SecretKeySpec;

import com.gerken.audioGuide.interfaces.AssetStreamProvider;

import android.content.Context;
import android.content.res.AssetManager;

public class GuideAssetManager implements AssetStreamProvider {
	private Context _context;
	private byte[] _seedBytes;
	
	private static final String _mkey = "abcdghijmnopstuv";
	
	public GuideAssetManager(Context context){
		_context = context;
		
		try {
			_seedBytes = _mkey.getBytes("ISO-8859-1");
		}
		catch(Exception e) {}
	}
	
	public InputStream getImageAssetStream(String imageName) throws Exception {
		AssetManager am = _context.getAssets();
		InputStream imageStream = am.open("images/" + imageName);
		CipherInputStream imageDecoderStream = 
				new CipherInputStream(imageStream, createCipher());
		return imageDecoderStream;
	}
	
	public InputStream getAudioAssetStream(String audioName) throws Exception {
		AssetManager am = _context.getAssets();
		InputStream imageStream = am.open(audioName);		
		return imageStream;
	}
	
	private Cipher createCipher() throws Exception {			
	    SecretKeySpec skeySpec = new SecretKeySpec(_seedBytes, "AES");
        Cipher cipher = Cipher.getInstance("AES");
        cipher.init(Cipher.DECRYPT_MODE, skeySpec);
    	return cipher;
	}
}
