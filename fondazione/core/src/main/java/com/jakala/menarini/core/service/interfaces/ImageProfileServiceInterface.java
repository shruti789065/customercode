package com.jakala.menarini.core.service.interfaces;

import com.jakala.menarini.core.dto.awslambda.ImageProfileServiceResponseDto;
import com.jakala.menarini.core.dto.awslambda.LambdaGetFileDto;
import com.jakala.menarini.core.dto.awslambda.LambdaPutFileDto;

public interface ImageProfileServiceInterface {

    public ImageProfileServiceResponseDto getImageProfile(LambdaGetFileDto getFileDto, String token);
    public ImageProfileServiceResponseDto saveImageProfile(LambdaPutFileDto putFileDto, String token);


}
