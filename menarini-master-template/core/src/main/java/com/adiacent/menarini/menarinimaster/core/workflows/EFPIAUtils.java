package com.adiacent.menarini.menarinimaster.core.workflows;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import org.apache.sling.api.resource.Resource;

import java.util.List;
import java.util.Map;

public class EFPIAUtils {

    private static final List<String> ACCEPTED_RESOURCE_TYPES = List.of("dam:asset");
    private static final List<String> ACCEPTED_FILE_EXT = List.of("jpg", "jpeg", "png");
    private static final String FILE_NAME_PATTERN = "^[0-9]{3}";
    private static final String PATH_PATTERN = "^\\/content\\/dam\\/([\\w-]+)\\/efpia\\/[\\w-]+\\/[0-9]{4}";

    private static final String DRAFT_PATH = "/content/dam/${siteName}/efpia/${reportName}/${year}";
    private static final String PUBLISH_PATH = "/content/dam/efpia/${siteName}/${reportName}/${year}";

    public static boolean isPathValid(String path) {
        if (path != null)
            return path.matches(PATH_PATTERN);
        return false;
    }

    public static String siteNameFromPath(String path) {
        if (isPathValid(path))
            return StringUtils.substringBefore(StringUtils.substringAfter(path, "/content/dam/"), "/");
        return null;
    }
    public static String reportNameFromPath(String path) {
        if (isPathValid(path)){
            String[] parts = StringUtils.split(path,"/");
            return parts[parts.length-2];
        }
        return null;
    }

    public static Integer yearFromPath(String path) {
        if (isPathValid(path)){
            String[] parts = StringUtils.split(path,"/");
            return Integer.parseInt(parts[parts.length-1]);
        }
        return null;
    }

    public static boolean isResourceTypeValid(String resourceType) {
        return ACCEPTED_RESOURCE_TYPES.contains(StringUtils.lowerCase(resourceType));
    }

    public static boolean isFileTypeValid(String ext) {
        return ACCEPTED_FILE_EXT.contains(StringUtils.lowerCase(ext));
    }

    public static boolean isFileNameValid(String fileName) {
        if (fileName != null)
            return fileName.matches(FILE_NAME_PATTERN);
        return false;
    }

    public static void validateResource(Resource asset) throws EFPIABlockingValidationException, EFPIANonBlockingValidationException {
        if (EFPIAUtils.isResourceTypeValid(asset.getResourceType())) {
            String fileName = StringUtils.substringBeforeLast(asset.getName(), ".");
            String fileExt = StringUtils.lowerCase(StringUtils.substringAfterLast(asset.getName(), "."));
            if (EFPIAUtils.isFileTypeValid(fileExt)) {
                if (!EFPIAUtils.isFileNameValid(fileName)) {
                    throw new EFPIABlockingValidationException(String.format("File name '%s' not acceptable", fileName));
                }
            } else {
                throw new EFPIABlockingValidationException(String.format("File extension %s is not allowed. Asset %s rejected", fileExt, asset.getName()));
            }
        } else {
            throw new EFPIANonBlockingValidationException(String.format("Resource type %s for resource %s is not acceptable. Skipped.", asset.getResourceType(), asset.getName()));
        }
    }

    public static String getDraftPath(String siteName, String reportName, String year) {
        return StringSubstitutor.replace(DRAFT_PATH, Map.of("siteName", siteName,"reportName",reportName, "year", year));
    }

    public static String getPublishPath(String siteName,String reportName, Integer year) {
        return StringSubstitutor.replace(PUBLISH_PATH, Map.of("siteName", siteName,"reportName",reportName, "year", year));
    }

    public static class EFPIABlockingValidationException extends Throwable {
        public EFPIABlockingValidationException(String message) {
            super(message);
        }
    }

    public static class EFPIANonBlockingValidationException extends Throwable {
        public EFPIANonBlockingValidationException(String message) {
            super(message);
        }
    }

}
