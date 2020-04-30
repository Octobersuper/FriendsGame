package com.zcf.framework.common.exception;

import com.zcf.framework.common.json.Body;

/**
 * json 运行异常
 *
 */
public abstract class JsonRuntimeException extends RuntimeException {
	private static final long serialVersionUID = -4986615066710371014L;

	public abstract Body getBody();
}
