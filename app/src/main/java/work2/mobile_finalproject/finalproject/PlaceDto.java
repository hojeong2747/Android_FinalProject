package work2.mobile_finalproject.finalproject;

import java.io.Serializable;

public class PlaceDto implements Serializable {
    private long id;
    private String name;
    private String address;
    private String uri;
    private String phone;

    private String date;
    private String photoPath;
    private String content;
    private float rating;

    private String placeId;
    private double lat;
    private double lng;
    private String keyWord;

    public PlaceDto() { }

    // 즐겨찾기
    public PlaceDto(long id, String name, String address, String uri, String placeId, double lat, double lng, String keyWord) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.uri = uri;
        this.placeId = placeId;
        this.lat = lat;
        this.lng = lng;
        this.keyWord = keyWord;
    }

    // 리뷰
    public PlaceDto(long id, String name, String address, String uri, String date, String photoPath, String content, float rating) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.uri = uri;
        this.date = date;
        this.photoPath = photoPath;
        this.content = content;
        this.rating = rating;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getPhotoPath() {
        return photoPath;
    }

    public void setPhotoPath(String photoPath) {
        this.photoPath = photoPath;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public float getRating() {
        return rating;
    }

    public void setRating(float rating) {
        this.rating = rating;
    }

    public String getPlaceId() {
        return placeId;
    }

    public void setPlaceId(String placeId) {
        this.placeId = placeId;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getKeyWord() {
        return keyWord;
    }

    public void setKeyWord(String keyWord) {
        this.keyWord = keyWord;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
