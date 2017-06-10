package getfun.com.br.getfun.domain;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;


public class Event implements Parcelable {
    private String nome;
    private String tipo;
    private String description;
    private int category;
    private String tel;
    private int photo;
    private String website;
    private String maps;


    public Event(){}
    public Event(String n, String t, int p){
        nome = n;
        tipo = t;
        photo = p;
    }


    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public int getPhoto() {
        return photo;
    }

    public void setPhoto(int photo) {
        this.photo = photo;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getMaps() {
        return maps;
    }

    public void setMaps(String maps) {
        this.maps = maps;
    }

    public int getCategory() {
        return category;
    }

    public void setCategory(int category) {
        this.category = category;
    }


    // PARCELABLE
    public Event(Parcel parcel){
        setNome(parcel.readString());
        setTipo(parcel.readString());
        setDescription(parcel.readString());
        setCategory(parcel.readInt());
        setTel(parcel.readString());
        setWebsite(parcel.readString());
        setMaps(parcel.readString());
        setPhoto(parcel.readInt());
    }
    @Override
    public int describeContents() {
        return 0;
    }
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString( getNome() );
        dest.writeString( getTipo() );
        dest.writeString( getDescription() );
        dest.writeInt( getCategory() );
        dest.writeString( getTel() );
        dest.writeString( getWebsite() );
        dest.writeString( getMaps() );
        dest.writeInt( getPhoto() );
    }
    public static final Parcelable.Creator<Event> CREATOR = new Parcelable.Creator<Event>(){
        @Override
        public Event createFromParcel(Parcel source) {
            return new Event(source);
        }
        @Override
        public Event[] newArray(int size) {
            return new Event[size];
        }
    };
}
