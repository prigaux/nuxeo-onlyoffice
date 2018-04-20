package fr.edu.lyon.nuxeo.jwt.service;

import java.util.Map;

public interface PayloadResolver
{
	String getUserId(Map<String, Object> payload);
}
