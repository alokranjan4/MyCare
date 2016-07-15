package com.ibm.services.vo;

import org.springframework.web.multipart.MultipartFile;

public class UpdatePackage {

	private String PackageType;
	public String getPackageType() {
		return PackageType;
	}
	public void setPackageType(String packageType) {
		PackageType = packageType;
	}
	public String getPackageCategory() {
		return PackageCategory;
	}
	public void setPackageCategory(String packageCategory) {
		PackageCategory = packageCategory;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public String getPackageCategoryID() {
		return packageCategoryID;
	}
	public void setPackageCategoryID(String packageCategoryID) {
		this.packageCategoryID = packageCategoryID;
	}
	public String getCatSeq() {
		return catSeq;
	}
	public void setCatSeq(String catSeq) {
		this.catSeq = catSeq;
	}
	public MultipartFile getBannerImage() {
		return BannerImage;
	}
	public void setBannerImage(MultipartFile bannerImage) {
		BannerImage = bannerImage;
	}
	private String PackageCategory;
	private String description;
	private String packageCategoryID;
	private String catSeq;
	private MultipartFile BannerImage;
	
	
	
}
