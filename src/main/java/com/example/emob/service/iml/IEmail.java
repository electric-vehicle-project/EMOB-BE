package com.example.emob.service.iml;

public interface IEmail {
    void sendEmail (String headerType, String title, String subTitle, String icon, String greeting, String buttonUrl,
                                                String content, String alert, String customer, String buttonName, String toEmail);
}
