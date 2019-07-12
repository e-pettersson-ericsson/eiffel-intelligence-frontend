/*
   Copyright 2018 Ericsson AB.
   For a full list of individual contributors, please see the commit history.

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.
*/
package com.ericsson.ei.frontend.utils;

import java.io.IOException;
import java.util.*;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ericsson.ei.frontend.model.BackEndInformation;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
public class BackEndInstancesUtils {

    private static final Logger LOG = LoggerFactory.getLogger(BackEndInstancesUtils.class);

    public static final String NAME = "name";
    public static final String HOST = "host";
    public static final String PORT = "port";
    public static final String CONTEXT_PATH = "contextPath";
    public static final String HTTPS = "https";
    public static final String DEFAULT = "defaultBackend";
    public static final List<String> ALLOWED_JSON_KEYS = Arrays.asList("name",
            "host", "port", "contextPath", "https", "defaultBackend");

    private static final long SECONDS_BETWEEN_PARSING = 20;

    private String defaultBackEndInstanceName;

    @Autowired
    private BackEndInformation defaultBackendInformation;

    @Autowired
    private BackEndInstanceFileUtils backEndInstanceFileUtils;

    private List<BackEndInformation> backEndInformationList = new ArrayList<>();
    private boolean currentlyParsing = false;
    private boolean isRunningTests = false;
    private long nextTimeToParse = 0;
    private boolean savedSinceLastParsing = false;

    /**
     * Checks that all required keys exist in the incoming json object.
     *
     * @param JsonObject instance
     * @return boolean
     * */
    public boolean hasRequiredJsonKeys(JsonObject instance) {
        if (instance.has(HOST) && instance.has(PORT) && instance.has(NAME)
                && instance.has(CONTEXT_PATH) && instance.has(HTTPS)) {
            return true;
        }
        return false;
    }

    /**
     * Checks if json values in backend instance are null.
     *
     * @param JsonObject instance
     * @return boolean
     * */
    public boolean containsNullValues(JsonObject instance) {
        for (Map.Entry<String, JsonElement> entrySet : instance.entrySet()) {
            if (entrySet.getValue().isJsonNull() || entrySet.getValue() == null) {
                LOG.debug(entrySet.toString() + " can not be null!");
                return true;
            }
        }
        return false;
    }

    /**
     * Checks that the incoming json data does not contain additional keys. Any
     * unrecognized keys are logged.
     *
     * @param JsonObject instance
     * @return boolean
     * */
    public boolean containsAdditionalKeys(JsonObject instance) {
        if (instance.size() > ALLOWED_JSON_KEYS.size()) {
            for (Map.Entry<String, JsonElement> entrySet : instance.entrySet()) {

                boolean hasUnrecognizedKeys = !ALLOWED_JSON_KEYS.contains(entrySet.getKey());
                if (hasUnrecognizedKeys) {
                    LOG.debug("Unrecognized key " + entrySet.getKey());
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Returns true or false depending if instance host, port, contextPath or https already exist.
     * Will return true if all values are the same, false if any value is different, thus input instance
     * details are unique.
     *
     * @param JsonObject
     * @return boolean
     */
    public boolean checkIfInstanceAlreadyExist(JsonObject instance) {
        parseBackEndInstances();

        for (BackEndInformation backendInformation : backEndInformationList) {
            // Ensure unique host, port and context paths
            if (backendInformation.getHost().equals(instance.get(HOST).getAsString())
                    && Integer.valueOf(backendInformation.getPort()) == instance.get(PORT).getAsInt()
                    && backendInformation.getContextPath().equals(instance.get(CONTEXT_PATH).getAsString())
                    && backendInformation.isUseSecureHttpBackend() == instance.get(HTTPS).getAsBoolean()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true or false depending if the name is unique, the name is an identifier thus must be
     * unique.
     *
     * @param JsonObject
     * @return boolean
     */
    public boolean checkIfInstanceNameAlreadyExist(JsonObject instance) {
        parseBackEndInstances();
        for (BackEndInformation backendInformation : backEndInformationList) {
            if (backendInformation.getName().equals(instance.get(NAME).getAsString())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns true or false depending if default instance exist.
     *
     * @return boolean
     */
    public boolean hasDefaultBackend() {
        for (BackEndInformation backendInformation : backEndInformationList) {
            if (backendInformation.isDefaultBackend()) {
                return true;
            }
        }
        return false;
    }

    /**
     * Returns the BackEndInformation based on input name.
     *
     * @param String backEndName
     * @return backendInformation if exist null if no backendInformation exist
     */
    public BackEndInformation getBackEndInformationByName(String backEndName) {
        LOG.debug("getBackEndInformationByName called for with name '" + backEndName + "'.");

        parseBackEndInstances();

        for (BackEndInformation backendInformation : backEndInformationList) {
            if (backendInformation.getName().equals(backEndName)) {
                LOG.debug("Returning named BackEndInformation.");
                return backendInformation;
            }
        }

        if (getDefaultBackendInformation().getHost() != null && getDefaultBackendInformation().getPort() != null) {
            LOG.debug("Returning (default) BackEndInformation.");
            return getDefaultBackendInformation();
        }

        if (backEndInformationList.size() == 0) {
            return null;
        }

        LOG.debug("Returning (first present) BackEndInformation.");
        return backEndInformationList.get(0);
    }

    /**
     * Adds a new back end to the backEndInformationList and saves the data.
     *
     * @param instance back end information as JsonObject
     */
    public void addNewBackEnd(JsonObject instance) throws IOException {
        parseBackEndInstances();
        backEndInformationList.add(new ObjectMapper().readValue(instance.toString(), BackEndInformation.class));
        saveBackEndInformationList();
    }

    /**
     * Deletes a back end from the backEndInformationList and saves the new list.
     *
     * @param objectToDelete back end information as JsonObject
     */
    public void deleteBackEnd(JsonObject objectToDelete) {
        parseBackEndInstances();
        for (BackEndInformation backendInformation : backEndInformationList) {
            if (backendInformation.getName().equals(objectToDelete.get(NAME).getAsString())
                    && backendInformation.getHost().equals(objectToDelete.get(HOST).getAsString())
                    && backendInformation.getPort().equals(objectToDelete.get(PORT).getAsString())
                    && backendInformation.getContextPath().equals(objectToDelete.get(CONTEXT_PATH).getAsString())
                    && backendInformation.isUseSecureHttpBackend() == objectToDelete.get(HTTPS).getAsBoolean()
                    && !backendInformation.isDefaultBackend()) {
                backEndInformationList.remove(backendInformation);
                saveBackEndInformationList();
                break;
            }
        }
    }

    /**
     * Returns the bakckEndInformationList parsed to JsonArray.
     *
     * @return JsonArray with back end information data.
     */
    public JsonArray getBackEndsAsJsonArray() {
        LOG.debug("Read and return EI Backend instances as JsonArray.");
        parseBackEndInstances();
        return parseBackEndsAsJsonArray();
    }

    /**
     * Quick function to set default back end to null
     */
    public void setDefaultBackEndInstanceToNull() {
        setDefaultBackEndInstance(null, null, 0, null, false);
    }

    /**
     * Function that may be used to set default back end.
     *
     * @param name
     * @param host
     * @param port
     * @param contextPath
     * @param def
     */
    public void setDefaultBackEndInstance(String name, String host, int port, String contextPath, boolean def) {
        LOG.debug("Setting default EI Instance: " + name + ", " + host + ":" + port);
        getDefaultBackendInformation().setName(name);
        getDefaultBackendInformation().setHost(host);
        getDefaultBackendInformation().setPort(String.valueOf(port));
        getDefaultBackendInformation().setContextPath(contextPath);
        getDefaultBackendInformation().setUseSecureHttpBackend(false);
        getDefaultBackendInformation().setDefaultBackend(def);
    }

    private JsonArray parseBackEndsAsJsonArray() {
        JsonArray backEndList = new JsonArray();
        for (BackEndInformation backendInformation : backEndInformationList) {
            backEndList.add(backendInformation.getAsJsonObject());
        }
        return backEndList;
    }

    private void saveBackEndInformationList() {
        JsonArray jsonArrayToDump = parseBackEndsAsJsonArray();
        backEndInstanceFileUtils.dumpJsonArray(jsonArrayToDump);
        savedSinceLastParsing = true;
    }

    private void parseBackEndInstances() {
        if (!parsingIsApplicable()) {
            return;
        }
        currentlyParsing = true;

        try {
            JsonArray instances = backEndInstanceFileUtils.getInstancesFromFile();
            backEndInformationList.clear();
            for (JsonElement element : instances) {
                backEndInformationList.add(new ObjectMapper().readValue(element.toString(), BackEndInformation.class));
            }
            ensureDefaultBackEnd();
        } catch (IOException e) {
            LOG.error("Failure when trying to parse json " + e.getMessage());
        }

        currentlyParsing = false;
        savedSinceLastParsing = false;
        nextTimeToParse = System.currentTimeMillis() + (SECONDS_BETWEEN_PARSING * 1000);
    }

    private boolean parsingIsApplicable() {
        /**
         * If this is a test and test is dependent on parsing to be executed we want to
         * parse.
         */
        if (isRunningTests) {
            return true;
        }

        /**
         * If parsing is ongoing wait for it to finish, we do not parse again since it
         * should already be up to date.
         */
        if (currentlyParsing) {
            long stopTime = System.currentTimeMillis() + 10000;
            while (currentlyParsing && stopTime > System.currentTimeMillis()) {
                try {
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                }
            }
            return false;
        }

        /**
         * If parsing has not been done for a set amount of time, then we want to parse.
         */
        if (nextTimeToParse <= System.currentTimeMillis()) {
            return true;
        }

        /**
         * If an update has happened to the file, then we should parse the file.
         */
        if (savedSinceLastParsing) {
            return true;
        }

        return false;
    }

    private void ensureDefaultBackEnd() {
        if (defaultBackendInformation.getHost() == null || defaultBackendInformation.getPort() == null) {
            LOG.debug("No default EI Backend instance Host and/or Port has been set!");
            return;
        }
        defaultBackendInformation.setDefaultBackend(true);
        for (BackEndInformation information : backEndInformationList) {
            if (defaultBackendInformation.getHost().equals(information.getHost())
                    && defaultBackendInformation.getPort().equals(information.getPort())
                    && information.isDefaultBackend()) {
                LOG.debug("Default back end already set!");
                return;
            }
        }
        backEndInformationList.add(defaultBackendInformation);
    }
}