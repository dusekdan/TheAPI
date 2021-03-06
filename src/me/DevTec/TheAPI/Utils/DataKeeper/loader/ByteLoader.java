package me.DevTec.TheAPI.Utils.DataKeeper.loader;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.zip.GZIPInputStream;

import me.DevTec.TheAPI.Utils.DataKeeper.Data.DataHolder;
import me.DevTec.TheAPI.Utils.Json.jsonmaker.Maker;

public class ByteLoader implements DataLoader {
	private HashMap<String, DataHolder> data = new HashMap<>();
	private boolean l;
	@Override
	public HashMap<String, DataHolder> get() {
		return data;
	}
	
	@Override
	public void load(String input) {
		data.clear();
		synchronized(this) {
		try {
			byte[] bb = Base64.getDecoder().decode(input);
			InputStream ousr = null;
			try {
				ousr = new ObjectInputStream(new GZIPInputStream(new ByteArrayInputStream(bb)));
			}catch(Exception er) {
				ousr = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(bb))));
			}
			while(true)
				try {
					String key = (ousr instanceof DataInputStream?(DataInputStream)ousr : (ObjectInputStream)ousr).readUTF();
					data.put(key, new DataHolder(Maker.objectFromJson((ousr instanceof DataInputStream?(DataInputStream)ousr : (ObjectInputStream)ousr).readUTF())));
				}catch(Exception e) {
				break;
				}
			ousr.close();
			l=true;
			}catch(Exception er) {
			try {
				DataInputStream ousr = new DataInputStream(new BufferedInputStream(new GZIPInputStream(new ByteArrayInputStream(input.getBytes()))));
				while(true)
					try {
						String key = ousr.readUTF();
						data.put(key, new DataHolder(Maker.objectFromJson(ousr.readUTF())));
					}catch(Exception e) {
					break;
					}
				ousr.close();
				l=true;
				}catch(Exception err) {
				l=false;
			}
		}}
	}

	@Override
	public List<String> getHeader() {
		//NOT SUPPORTED
		return null;
	}

	@Override
	public List<String> getFooter() {
		//NOT SUPPORTED
		return null;
	}

	@Override
	public boolean loaded() {
		return l;
	}

	@Override
	public String getDataName() {
		return "Data(ByteLoader:"+data.size()+")";
	}
}
