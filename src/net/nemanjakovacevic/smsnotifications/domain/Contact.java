package net.nemanjakovacevic.smsnotifications.domain;

import android.graphics.Bitmap;


/**
 * Class containing contact data.
 * 
 * @author Nemanja Kovacevic
 *
 */
public class Contact {

	private int id;
	private String name;
	private String number;
	private Bitmap photo;
	
	public Contact() {
	}
	
	public Contact(int id, String name, String number) {
		super();
		this.id = id;
		this.name = name;
		this.number = number;
	}
	
	public void setPhoto(Bitmap bitmap) {
		this.photo = bitmap;
	}
	
	public Bitmap getPhoto() {
		return photo;
	}

	public int getId() {
		return id;
	}
	
	public void setId(int id) {
		this.id = id;
	}
	
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getNumber() {
		return number;
	}
	
	public void setNumber(String number) {
		this.number = number;
	}
			
	@Override
	public String toString() {
		if(name != null){
			return name + " (" + number + ")";
		}else{
			return number;
		}
	}
}
