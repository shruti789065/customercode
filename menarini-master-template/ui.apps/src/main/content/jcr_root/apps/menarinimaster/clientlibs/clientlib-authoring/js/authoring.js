/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2021 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
(function($, Granite) {
    "use strict";

    var dialogContentSelector = ".cmp-image__editor";
    var $dialogContent;
    var $videoGroup;
    var $manualDurationValue;
    

    $(document).on("dialog-loaded", function(e) {
        var $dialog        = e.dialog;
        $dialogContent = $dialog.find(dialogContentSelector);
        var dialogContent  = $dialogContent.length > 0 ? $dialogContent[0] : undefined;
        if (dialogContent) {
            isAutomaticDuration = dialogContent.querySelector('coral-checkbox[name="./automaticDuration"]');
            $videoGroup = $dialogContent.find(".cmp-video__editor-duration-group");
            $manualDurationValue = $dialogContent.find(".cmp-video__editor-duration-value");
        }
		$videoGroup.hide();
		//TODO  Aggiustare questa function
		//toggleAutomaticDurationFields(isAutomaticDuration);
    });

    $(document).on("change", dialogContentSelector + " coral-checkbox[name='./automaticDuration']", function(e) {
        toggleAutomaticDurationFields(e.target);
    });

    function toggleAutomaticDurationFields(isAutomaticCheckbox) {
        if (isAutomaticCheckbox) {
            if (isAutomaticCheckbox.checked) {
                $videoGroup.hide();
            } else {
                $videoGroup.show();
            }
        } else {
            $videoGroup.show();
        }
    }

})(jQuery, Granite);
