package com.adiacent.menarini.menarinimaster.core.beans;

import com.adiacent.menarini.menarinimaster.core.schedulers.EncodeDecodeSecretKey;
import com.adiacent.menarini.menarinimaster.core.utils.ModelUtils;
import lombok.Getter;

@Getter
public class EncryptedMail {
	private String encryptedMail = "";

	public void setEncryptedMail(String encryptedMail) throws Exception {
		this.encryptedMail = ModelUtils.decrypt(EncodeDecodeSecretKey.get_instance().getConfig().getSecretKey(),
				EncodeDecodeSecretKey.get_instance().getConfig().getIvParameter(),
				encryptedMail, EncodeDecodeSecretKey.get_instance().getConfig().getAlgorithm());
	}
}
