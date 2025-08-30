package com.sep490.gshop.service;

import com.sep490.gshop.payload.dto.HsCodeDTO;
import com.sep490.gshop.payload.dto.HsCodeSearchDTO;
import com.sep490.gshop.payload.dto.HsTreeNodeDTO;
import com.sep490.gshop.payload.request.HsCodeRequest;
import com.sep490.gshop.payload.response.ImportedResponse;
import com.sep490.gshop.payload.response.MessageResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface HsCodeService {
    Page<HsTreeNodeDTO> findAll(String hsCode, String description, int page, int size, Sort.Direction direction);

    HsCodeDTO createHsCodeIncludeTaxes(HsCodeRequest hsCodeRequest);
    HsCodeDTO getByHsCode(String hsCode);
    MessageResponse deleteHsCode(String hsCode);

    MessageResponse importHsCodeCSV(MultipartFile file);

    ImportedResponse importHsCodeNewPhase(List<HsCodeRequest> requests);
}
