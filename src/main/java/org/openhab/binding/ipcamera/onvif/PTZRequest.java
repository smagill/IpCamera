/**
 * Copyright (c) 2010-2019 Contributors to the openHAB project
 *
 * See the NOTICE file(s) distributed with this work for additional
 * information.
 *
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0
 *
 * SPDX-License-Identifier: EPL-2.0
 */

package org.openhab.binding.ipcamera.onvif;

import org.eclipse.jdt.annotation.Nullable;
import org.eclipse.smarthome.core.thing.binding.ThingHandler;
import org.openhab.binding.ipcamera.handler.IpCameraHandler;

import be.teletask.onvif.models.OnvifMediaProfile;
import be.teletask.onvif.models.OnvifType;
import be.teletask.onvif.requests.OnvifRequest;

/**
 * The {@link PTZRequest} is responsible for handling onvif PTZ commands.
 *
 * @author Matthew Skinner - Initial contribution
 */

public class PTZRequest implements OnvifRequest {
    String profileToken = "1";
    String requestType = "GetConfigurations";
    private IpCameraHandler thisCamera;

    public PTZRequest(String requestType, OnvifMediaProfile onvifMediaProfile) {
        this.requestType = requestType;
        profileToken = onvifMediaProfile.getToken();
    }

    public PTZRequest(String string, OnvifMediaProfile onvifMediaProfile, @Nullable ThingHandler handler) {
        this(string, onvifMediaProfile);
        thisCamera = (IpCameraHandler) handler;
    }

    @Override
    public String getXml() {
        switch (requestType) {
            case "GetConfigurations":
                return "<GetConfigurations xmlns=\"http://www.onvif.org/ver20/ptz/wsdl\"></GetConfigurations>";
            case "GetConfiguration":
                return "<GetConfiguration xmlns=\"http://www.onvif.org/ver20/ptz/wsdl\"><PTZConfigurationToken>000</PTZConfigurationToken></GetConfiguration>";
            case "AbsoluteMove": // needs handler in constructor!
                return "<AbsoluteMove xmlns=\"http://www.onvif.org/ver20/ptz/wsdl\"><ProfileToken>" + profileToken
                        + "</ProfileToken><Position><PanTilt x=\"" + thisCamera.currentPanCamValue + "\" y=\""
                        + thisCamera.currentTiltCamValue + "\" space=\"\">\n" + "                 </PanTilt>\n"
                        + "                 <Zoom x=\"" + thisCamera.currentZoomCamValue + "\" space=\"\">\n"
                        + "                 </Zoom>\n" + "                </Position>\n"
                        + "                <Speed><PanTilt x=\"0.0\" y=\"0.0\" space=\"\"></PanTilt><Zoom x=\"0.0\" space=\"\"></Zoom>\n"
                        + "                </Speed></AbsoluteMove>";
            case "GetNodes":
                return "<GetNodes xmlns=\"http://www.onvif.org/ver20/ptz/wsdl\"></GetNodes>";
            case "GetStatus":
                return "<GetStatus xmlns=\"http://www.onvif.org/ver20/ptz/wsdl\"><ProfileToken>" + profileToken
                        + "</ProfileToken></GetStatus>";
        }
        return "notfound";
    }

    @Override
    public OnvifType getType() {
        return OnvifType.CUSTOM;
    }

    public String getParsedResult(String result) {
        // to do, change below
        int beginIndex = result.indexOf("<tt:Uri>");
        int endIndex = result.indexOf("</tt:Uri>");
        if (beginIndex >= 0 && endIndex >= 0) {
            return result.substring(beginIndex, endIndex);
        } else {
            return "noUri";
        }
    }
}
