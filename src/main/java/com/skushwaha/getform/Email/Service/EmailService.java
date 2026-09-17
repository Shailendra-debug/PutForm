package com.skushwaha.getform.Email.Service;

import com.resend.Resend;
import com.resend.core.exception.ResendException;
import com.resend.services.emails.model.CreateEmailOptions;
import com.resend.services.emails.model.CreateEmailResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final Resend resend;

    @Value("${resend.from}")
    private String from;

    public void sendOtpForRegistration(
            String email,
            String otp
    ) {

        String html = """
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Verify Your Email - PutForm</title>
        </head>

        <body style="
            margin:0;
            padding:0;
            background-color:#F3F8F7;
            font-family:Arial, Helvetica, sans-serif;
            color:#172033;
        ">

        <!-- Background -->
        <table
            role="presentation"
            width="100%"
            cellspacing="0"
            cellpadding="0"
            border="0"
            style="
                width:100%;
                background-color:#F3F8F7;
            "
        >
            <tr>
                <td
                    align="center"
                    style="padding:35px 15px;"
                >

                    <!-- Main Card -->
                    <table
                        role="presentation"
                        width="600"
                        cellspacing="0"
                        cellpadding="0"
                        border="0"
                        style="
                            width:100%;
                            max-width:600px;
                            background-color:#FFFFFF;
                            border-radius:18px;
                            overflow:hidden;
                        "
                    >

                        <!-- Banner -->
                        <tr>
                            <td
                                align="center"
                                style="
                                    padding:0;
                                    margin:0;
                                    line-height:0;
                                    font-size:0;
                                "
                            >

                                <img
                                    src="https://file.putform.online/app/putformemil.png"
                                    alt="PutForm"
                                    width="600"
                                    style="
                                        display:block;
                                        width:100%;
                                        max-width:600px;
                                        height:auto;
                                        border:0;
                                        margin:0;
                                        padding:0;
                                    "
                                >

                            </td>
                        </tr>


                        <!-- Brand -->
                        <tr>
                            <td
                                align="center"
                                style="
                                    padding:28px 30px 5px 30px;
                                "
                            >

                                <table
                                    role="presentation"
                                    cellspacing="0"
                                    cellpadding="0"
                                    border="0"
                                >
                                    <tr>

                                        <td
                                            align="center"
                                            valign="middle"
                                            width="42"
                                            height="42"
                                            style="
                                                width:42px;
                                                height:42px;
                                                background-color:#E8F9F5;
                                                border-radius:10px;
                                                color:#2F7C75;
                                                font-size:20px;
                                                font-weight:bold;
                                                text-align:center;
                                            "
                                        >
                                            ✓
                                        </td>

                                        <td
                                            width="10"
                                            style="
                                                width:10px;
                                                font-size:0;
                                                line-height:0;
                                            "
                                        >
                                            &nbsp;
                                        </td>

                                        <td
                                            style="
                                                font-size:23px;
                                                font-weight:bold;
                                                color:#172033;
                                                vertical-align:middle;
                                            "
                                        >
                                            Put<span style="color:#4FCDB5;">Form</span>
                                        </td>

                                    </tr>
                                </table>

                            </td>
                        </tr>


                        <!-- Title -->
                        <tr>
                            <td
                                align="center"
                                style="
                                    padding:18px 35px 5px 35px;
                                "
                            >

                                <h1
                                    style="
                                        margin:0;
                                        padding:0;
                                        font-size:28px;
                                        line-height:36px;
                                        font-weight:700;
                                        color:#172033;
                                    "
                                >
                                    Verify Your Email
                                </h1>

                            </td>
                        </tr>


                        <!-- Description -->
                        <tr>
                            <td
                                align="center"
                                style="
                                    padding:8px 45px 25px 45px;
                                "
                            >

                                <p
                                    style="
                                        margin:0;
                                        padding:0;
                                        font-size:15px;
                                        line-height:24px;
                                        color:#64748B;
                                    "
                                >
                                    Welcome to PutForm!
                                </p>

                                <p
                                    style="
                                        margin:7px 0 0 0;
                                        padding:0;
                                        font-size:15px;
                                        line-height:24px;
                                        color:#64748B;
                                    "
                                >
                                    Use the verification code below to verify
                                    your email address.
                                </p>

                            </td>
                        </tr>


                        <!-- OTP Box -->
                        <tr>
                            <td
                                align="center"
                                style="
                                    padding:0 35px;
                                "
                            >

                                <table
                                    role="presentation"
                                    width="100%"
                                    cellspacing="0"
                                    cellpadding="0"
                                    border="0"
                                    style="
                                        width:100%;
                                        max-width:390px;
                                        background-color:#F0F9F7;
                                        border:1px solid #B8E5DC;
                                        border-radius:14px;
                                    "
                                >

                                    <tr>
                                        <td
                                            align="center"
                                            style="
                                                padding:20px 15px 8px 15px;
                                            "
                                        >

                                            <p
                                                style="
                                                    margin:0;
                                                    padding:0;
                                                    font-size:11px;
                                                    line-height:18px;
                                                    font-weight:bold;
                                                    letter-spacing:1.5px;
                                                    text-transform:uppercase;
                                                    color:#64748B;
                                                "
                                            >
                                                YOUR VERIFICATION CODE
                                            </p>

                                        </td>
                                    </tr>

                                    <tr>
                                        <td
                                            align="center"
                                            style="
                                                padding:0 15px 20px 15px;
                                            "
                                        >

                                            <p
                                                style="
                                                    margin:0;
                                                    padding:0;
                                                    font-family:'Courier New',Courier,monospace;
                                                    font-size:34px;
                                                    line-height:42px;
                                                    font-weight:bold;
                                                    letter-spacing:7px;
                                                    color:#246B65;
                                                "
                                            >
                                                {{OTP}}
                                            </p>

                                        </td>
                                    </tr>

                                </table>

                            </td>
                        </tr>


                        <!-- Expiry -->
                        <tr>
                            <td
                                align="center"
                                style="
                                    padding:20px 35px 5px 35px;
                                "
                            >

                                <p
                                    style="
                                        margin:0;
                                        padding:0;
                                        font-size:14px;
                                        line-height:22px;
                                        color:#64748B;
                                    "
                                >
                                    This verification code expires in
                                    <strong style="color:#172033;">
                                        10 minutes
                                    </strong>.
                                </p>

                            </td>
                        </tr>


                        <!-- Divider -->
                        <tr>
                            <td
                                style="
                                    padding:20px 45px;
                                "
                            >

                                <table
                                    role="presentation"
                                    width="100%"
                                    cellspacing="0"
                                    cellpadding="0"
                                    border="0"
                                >
                                    <tr>
                                        <td
                                            style="
                                                height:1px;
                                                background-color:#E8EEEE;
                                                font-size:0;
                                                line-height:0;
                                            "
                                        >
                                            &nbsp;
                                        </td>
                                    </tr>
                                </table>

                            </td>
                        </tr>


                        <!-- Security Message -->
                        <tr>
                            <td
                                align="center"
                                style="
                                    padding:0 35px 30px 35px;
                                "
                            >

                                <table
                                    role="presentation"
                                    width="100%"
                                    cellspacing="0"
                                    cellpadding="0"
                                    border="0"
                                    style="
                                        width:100%;
                                        background-color:#F8FAFA;
                                        border-radius:12px;
                                    "
                                >

                                    <tr>
                                        <td
                                            align="center"
                                            style="
                                                padding:16px 20px;
                                            "
                                        >

                                            <p
                                                style="
                                                    margin:0;
                                                    padding:0;
                                                    font-size:13px;
                                                    line-height:21px;
                                                    color:#64748B;
                                                "
                                            >
                                                <strong style="color:#2F7C75;">
                                                    Security notice:
                                                </strong>
                                                Never share this verification
                                                code with anyone.
                                            </p>

                                        </td>
                                    </tr>

                                </table>

                            </td>
                        </tr>


                        <!-- Footer -->
                        <tr>
                            <td
                                align="center"
                                style="
                                    padding:25px 30px;
                                    background-color:#F8FAFA;
                                    border-top:1px solid #E8EEEE;
                                "
                            >

                                <p
                                    style="
                                        margin:0;
                                        padding:0;
                                        font-size:15px;
                                        line-height:22px;
                                        font-weight:bold;
                                        color:#2F7C75;
                                    "
                                >
                                    PutForm
                                </p>

                                <p
                                    style="
                                        margin:4px 0 0 0;
                                        padding:0;
                                        font-size:12px;
                                        line-height:18px;
                                        color:#94A3B8;
                                    "
                                >
                                    Create. Share. Collect.
                                </p>

                                <p
                                    style="
                                        margin:12px 0 0 0;
                                        padding:0;
                                        font-size:11px;
                                        line-height:17px;
                                        color:#A8B3B8;
                                    "
                                >
                                    © PutForm. All rights reserved.
                                </p>

                            </td>
                        </tr>

                    </table>

                </td>
            </tr>
        </table>

        </body>
        </html>
        """.replace("{{OTP}}", otp);

        sendEmail(
                email,
                "Verify your email - PutForm",
                html
        );
    }


    public void sendOtpForForgotPassword(
            String email,
            String otp
    ) {

        String html = """
        <!DOCTYPE html>
        <html lang="en">

        <head>
            <meta charset="UTF-8">
            <meta name="viewport" content="width=device-width, initial-scale=1.0">
            <title>Reset Your Password - PutForm</title>
        </head>

        <body style="
            margin:0;
            padding:0;
            background-color:#F3F8F7;
            font-family:Arial, Helvetica, sans-serif;
            color:#172033;
        ">

        <!-- Background -->
        <table
            role="presentation"
            width="100%"
            cellspacing="0"
            cellpadding="0"
            border="0"
            style="
                width:100%;
                background-color:#F3F8F7;
            "
        >
            <tr>
                <td
                    align="center"
                    style="
                        padding:35px 15px;
                    "
                >

                    <!-- Main Card -->
                    <table
                        role="presentation"
                        width="600"
                        cellspacing="0"
                        cellpadding="0"
                        border="0"
                        style="
                            width:100%;
                            max-width:600px;
                            background-color:#FFFFFF;
                            border-radius:18px;
                            overflow:hidden;
                        "
                    >

                        <!-- =================================
                             BANNER
                        ================================== -->
                        <tr>
                            <td
                                align="center"
                                style="
                                    padding:0;
                                    margin:0;
                                    line-height:0;
                                    font-size:0;
                                "
                            >

                                <img
                                    src="https://pub-a775e5ad72564a7f95c5cf745f35b2df.r2.dev/app/putformemil.png"
                                    alt="PutForm"
                                    width="600"
                                    style="
                                        display:block;
                                        width:100%;
                                        max-width:600px;
                                        height:auto;
                                        border:0;
                                        margin:0;
                                        padding:0;
                                    "
                                >

                            </td>
                        </tr>

                        <!-- =================================
                             TITLE
                        ================================== -->
                        <tr>
                            <td
                                align="center"
                                style="
                                    padding:18px 35px 5px 35px;
                                "
                            >

                                <h1
                                    style="
                                        margin:0;
                                        padding:0;
                                        font-size:28px;
                                        line-height:36px;
                                        font-weight:700;
                                        color:#172033;
                                    "
                                >
                                    Reset Your Password
                                </h1>

                            </td>
                        </tr>


                        <!-- =================================
                             DESCRIPTION
                        ================================== -->
                        <tr>
                            <td
                                align="center"
                                style="
                                    padding:8px 45px 25px 45px;
                                "
                            >

                                <p
                                    style="
                                        margin:0;
                                        padding:0;
                                        font-size:15px;
                                        line-height:24px;
                                        color:#64748B;
                                    "
                                >
                                    We received a request to reset your
                                    PutForm account password.
                                </p>

                                <p
                                    style="
                                        margin:7px 0 0 0;
                                        padding:0;
                                        font-size:15px;
                                        line-height:24px;
                                        color:#64748B;
                                    "
                                >
                                    Use the verification code below to continue.
                                </p>

                            </td>
                        </tr>


                        <!-- =================================
                             OTP BOX
                        ================================== -->
                        <tr>
                            <td
                                align="center"
                                style="
                                    padding:0 35px;
                                "
                            >

                                <table
                                    role="presentation"
                                    width="100%"
                                    cellspacing="0"
                                    cellpadding="0"
                                    border="0"
                                    style="
                                        width:100%;
                                        max-width:390px;
                                        background-color:#F0F9F7;
                                        border:1px solid #B8E5DC;
                                        border-radius:14px;
                                    "
                                >

                                    <tr>
                                        <td
                                            align="center"
                                            style="
                                                padding:20px 15px 8px 15px;
                                            "
                                        >

                                            <p
                                                style="
                                                    margin:0;
                                                    padding:0;
                                                    font-size:11px;
                                                    line-height:18px;
                                                    font-weight:bold;
                                                    letter-spacing:1.5px;
                                                    text-transform:uppercase;
                                                    color:#64748B;
                                                "
                                            >
                                                YOUR VERIFICATION CODE
                                            </p>

                                        </td>
                                    </tr>

                                    <tr>
                                        <td
                                            align="center"
                                            style="
                                                padding:0 15px 20px 15px;
                                            "
                                        >

                                            <p
                                                style="
                                                    margin:0;
                                                    padding:0;
                                                    font-family:'Courier New',Courier,monospace;
                                                    font-size:34px;
                                                    line-height:42px;
                                                    font-weight:bold;
                                                    letter-spacing:7px;
                                                    color:#246B65;
                                                "
                                            >
                                                {{OTP}}
                                            </p>

                                        </td>
                                    </tr>

                                </table>

                            </td>
                        </tr>


                        <!-- =================================
                             EXPIRY
                        ================================== -->
                        <tr>
                            <td
                                align="center"
                                style="
                                    padding:20px 35px 5px 35px;
                                "
                            >

                                <p
                                    style="
                                        margin:0;
                                        padding:0;
                                        font-size:14px;
                                        line-height:22px;
                                        color:#64748B;
                                    "
                                >
                                    This verification code expires in
                                    <strong style="color:#172033;">
                                        10 minutes
                                    </strong>.
                                </p>

                            </td>
                        </tr>


                        <!-- =================================
                             DIVIDER
                        ================================== -->
                        <tr>
                            <td
                                style="
                                    padding:20px 45px;
                                "
                            >

                                <table
                                    role="presentation"
                                    width="100%"
                                    cellspacing="0"
                                    cellpadding="0"
                                    border="0"
                                >
                                    <tr>
                                        <td
                                            style="
                                                height:1px;
                                                background-color:#E8EEEE;
                                                font-size:0;
                                                line-height:0;
                                            "
                                        >
                                            &nbsp;
                                        </td>
                                    </tr>
                                </table>

                            </td>
                        </tr>


                        <!-- =================================
                             SECURITY MESSAGE
                        ================================== -->
                        <tr>
                            <td
                                align="center"
                                style="
                                    padding:0 35px 30px 35px;
                                "
                            >

                                <table
                                    role="presentation"
                                    width="100%"
                                    cellspacing="0"
                                    cellpadding="0"
                                    border="0"
                                    style="
                                        width:100%;
                                        background-color:#F8FAFA;
                                        border-radius:12px;
                                    "
                                >

                                    <tr>
                                        <td
                                            align="center"
                                            style="
                                                padding:16px 20px;
                                            "
                                        >

                                            <p
                                                style="
                                                    margin:0;
                                                    padding:0;
                                                    font-size:13px;
                                                    line-height:21px;
                                                    color:#64748B;
                                                "
                                            >
                                                <strong style="color:#2F7C75;">
                                                    Security notice:
                                                </strong>
                                                If you did not request a password
                                                reset, you can safely ignore this email.
                                            </p>

                                        </td>
                                    </tr>

                                </table>

                            </td>
                        </tr>


                        <!-- =================================
                             FOOTER
                        ================================== -->
                        <tr>
                            <td
                                align="center"
                                style="
                                    padding:25px 30px;
                                    background-color:#F8FAFA;
                                    border-top:1px solid #E8EEEE;
                                "
                            >

                                <p
                                    style="
                                        margin:12px 0 0 0;
                                        padding:0;
                                        font-size:11px;
                                        line-height:17px;
                                        color:#A8B3B8;
                                    "
                                >
                                    © PutForm. All rights reserved.
                                </p>

                            </td>
                        </tr>

                    </table>

                </td>
            </tr>
        </table>

        </body>
        </html>
        """.replace("{{OTP}}", otp);

        sendEmail(
                email,
                "Reset your password - PutForm",
                html
        );
    }


    public String sendEmail(
            String to,
            String subject,
            String html
    ) {

        CreateEmailOptions email = CreateEmailOptions.builder()
                .from(from)
                .to(to)
                .subject(subject)
                .html(html)
                .build();

        try {
            CreateEmailResponse response =
                    resend.emails().send(email);

            return response.getId();

        } catch (ResendException e) {
            throw new RuntimeException(
                    "Failed to send email: " + e.getMessage(),
                    e
            );
        }
    }


}
