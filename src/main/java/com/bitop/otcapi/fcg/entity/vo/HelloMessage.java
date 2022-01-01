package com.bitop.otcapi.fcg.entity.vo;

import lombok.Data;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
public class HelloMessage {

    private String name;

	/*public HelloMessage() {
	}

	public HelloMessage(String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}*/

}
