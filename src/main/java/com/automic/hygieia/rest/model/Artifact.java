package com.automic.hygieia.rest.model;

import java.util.List;

import com.google.gson.annotations.SerializedName;

import lombok.Data;
import lombok.Getter;

public class Artifact extends BaseEntityInfo {
	@Getter
	@SerializedName("files")
	private List<ArtifactFile> files;

	@Data
	public class ArtifactFile {
		@SerializedName("file_path")
		private String filePath;
		@SerializedName("file_url")
		private String fileUrl;
		@SerializedName("checksum")
		private String checksum;
	}
}
