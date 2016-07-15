package com.ibm.services.vo;

import org.springframework.web.multipart.MultipartFile;

public class PackageInfor {
	
	
	
	@Override
	public String toString() {
		return "PackageInfor [PackageCode=" + PackageCode + ", Tariff=" + Tariff + ", OfferNameID=" + OfferNameID
				+ ", OfferNameEN=" + OfferNameEN + ", BenefitEN=" + BenefitEN + ", BenefitID=" + BenefitID
				+ ", Keyword=" + Keyword + ", Param=" + Param + ", offerLink=" + offerLink + ", OfferType=" + OfferType
				+ ", CustomerType=" + CustomerType + ", BannerImageID=" + BannerImageID + ", BannerImageEN="
				+ BannerImageEN + ", OfferID=" + OfferID + "]";
	}
	public String getPackageCode() {
		return PackageCode;
	}
	public void setPackageCode(String packageCode) {
		PackageCode = packageCode;
	}
	public String getTariff() {
		return Tariff;
	}
	public void setTariff(String tariff) {
		Tariff = tariff;
	}
	public String getOfferNameID() {
		return OfferNameID;
	}
	public void setOfferNameID(String offerNameID) {
		OfferNameID = offerNameID;
	}
	public String getOfferNameEN() {
		return OfferNameEN;
	}
	public void setOfferNameEN(String offerNameEN) {
		OfferNameEN = offerNameEN;
	}
	public String getBenefitEN() {
		return BenefitEN;
	}
	public void setBenefitEN(String benefitEN) {
		BenefitEN = benefitEN;
	}
	public String getBenefitID() {
		return BenefitID;
	}
	public void setBenefitID(String benefitID) {
		BenefitID = benefitID;
	}
	public String getKeyword() {
		return Keyword;
	}
	public void setKeyword(String keyword) {
		Keyword = keyword;
	}
	public String getParam() {
		return Param;
	}
	public void setParam(String param) {
		Param = param;
	}
	public String getOfferLink() {
		return offerLink;
	}
	public void setOfferLink(String offerLink) {
		this.offerLink = offerLink;
	}
	public String getOfferType() {
		return OfferType;
	}
	public void setOfferType(String offerType) {
		OfferType = offerType;
	}
	public String getCustomerType() {
		return CustomerType;
	}
	public void setCustomerType(String customerType) {
		CustomerType = customerType;
	}
	public MultipartFile getBannerImageID() {
		return BannerImageID;
	}
	public void setBannerImageID(MultipartFile bannerImageID) {
		BannerImageID = bannerImageID;
	}
	public MultipartFile getBannerImageEN() {
		return BannerImageEN;
	}
	public void setBannerImageEN(MultipartFile bannerImageEN) {
		BannerImageEN = bannerImageEN;
	}
	public String getOfferID() {
		return OfferID;
	}
	public void setOfferID(String offerID) {
		OfferID = offerID;
	}
	private String PackageCode;
	private String Tariff;
	private String OfferNameID;
	private String OfferNameEN;
	private String BenefitEN;

	private String BenefitID;
	private String Keyword;
	private String Param;
	private String offerLink;
	private String OfferType;

	private String CustomerType;
	private MultipartFile BannerImageID;
	private MultipartFile BannerImageEN;
	private String OfferID;
	
	

}
