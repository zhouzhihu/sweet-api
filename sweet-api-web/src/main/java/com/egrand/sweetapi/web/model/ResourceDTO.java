package com.egrand.sweetapi.web.model;

import lombok.Data;

/**
 * 选择的资源
 */
@Data
public class ResourceDTO {

	private String id;

	private String type;

	private String parentId;
}
