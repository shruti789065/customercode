package com.jakala.menarini.core.service.interfaces;

import com.jakala.menarini.core.dto.aswLambdaDto.ImageProfileServiceResponseDto;
import com.jakala.menarini.core.dto.aswLambdaDto.LambdaGetFileDto;
import com.jakala.menarini.core.dto.aswLambdaDto.LambdaPutFileDto;

public interface ImageProfileServiceInterface {

    public ImageProfileServiceResponseDto getImageProfile(LambdaGetFileDto getFileDto, String token);
    public ImageProfileServiceResponseDto saveImageProfile(LambdaPutFileDto putFileDto, String token);


}
