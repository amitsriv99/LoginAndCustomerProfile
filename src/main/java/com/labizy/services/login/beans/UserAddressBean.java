package com.labizy.services.login.beans;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserAddressBean {
	private String addressId;
	private String houseOrFlatNumber;
	private String houseOrApartmentName;
	private String streetAddress;
	private String localityName;
	private String city;
	private String town;
	private String village;
	private String state;
	private String country;
	private String pinCode;
	private String landmark;
	private String longitude;
	private String latitude;
	private String isPrimaryAddress;
	private String isBillingAddress;

	public String getAddressId() {
		return addressId;
	}
	public void setAddressId(String addressId) {
		this.addressId = addressId;
	}
	public String getHouseOrFlatNumber() {
		return houseOrFlatNumber;
	}
	public void setHouseOrFlatNumber(String houseOrFlatNumber) {
		this.houseOrFlatNumber = houseOrFlatNumber;
	}
	public String getHouseOrApartmentName() {
		return houseOrApartmentName;
	}
	public void setHouseOrApartmentName(String houseOrApartmentName) {
		this.houseOrApartmentName = houseOrApartmentName;
	}
	public String getStreetAddress() {
		return streetAddress;
	}
	public void setStreetAddress(String streetAddress) {
		this.streetAddress = streetAddress;
	}
	public String getLocalityName() {
		return localityName;
	}
	public void setLocalityName(String localityName) {
		this.localityName = localityName;
	}
	public String getCity() {
		return city;
	}
	public void setCity(String city) {
		this.city = city;
	}
	public String getTown() {
		return town;
	}
	public void setTown(String town) {
		this.town = town;
	}
	public String getVillage() {
		return village;
	}
	public void setVillage(String village) {
		this.village = village;
	}
	public String getState() {
		return state;
	}
	public void setState(String state) {
		this.state = state;
	}
	public String getCountry() {
		return country;
	}
	public void setCountry(String country) {
		this.country = country;
	}
	public String getPinCode() {
		return pinCode;
	}
	public void setPinCode(String pinCode) {
		this.pinCode = pinCode;
	}
	public String getLandmark() {
		return landmark;
	}
	public void setLandmark(String landmark) {
		this.landmark = landmark;
	}
	public String getIsPrimaryAddress() {
		return isPrimaryAddress;
	}
	public void setIsPrimaryAddress(String isPrimaryAddress) {
		this.isPrimaryAddress = isPrimaryAddress;
	}
	public String getIsBillingAddress() {
		return isBillingAddress;
	}
	public void setIsBillingAddress(String isBillingAddress) {
		this.isBillingAddress = isBillingAddress;
	}
	public String getLongitude() {
		return longitude;
	}
	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}
	public String getLatitude() {
		return latitude;
	}
	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}
}
