package com.nashtech.rootkies.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import com.nashtech.rootkies.dto.common.ResponseDTO;

@ControllerAdvice
public class GlobalExceptionHandler {
	@ExceptionHandler(ConvertEntityDTOException.class)
	public ResponseEntity convertEntityDTOException(ConvertEntityDTOException ex, WebRequest request) {
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ConverterUserException.class)
	public ResponseEntity converterUserException(ConverterUserException ex, WebRequest request) {
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(CreateDataFailException.class)
	public ResponseEntity createDataFailException(CreateDataFailException ex, WebRequest request) {
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DataNotFoundException.class)
	public ResponseEntity dataNotFoundException(DataNotFoundException ex, WebRequest request) {
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(DeleteDataFailException.class)
	public ResponseEntity deleteDataFailException(DeleteDataFailException ex, WebRequest request) {
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(DuplicateDataException.class)
	public ResponseEntity duplicateDataException(DuplicateDataException ex, WebRequest request) {
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidRequestDataException.class)
	public ResponseEntity invalidRequestDataException(InvalidRequestDataException ex, WebRequest request) {
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ResourceNotFoundException.class)
	public ResponseEntity resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(UpdateDataFailException.class)
	public ResponseEntity updateDataFailException(UpdateDataFailException ex, WebRequest request) {
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UserAuthenticationException.class)
	public ResponseEntity userAuthenticationException(UserAuthenticationException ex, WebRequest request) {
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.UNAUTHORIZED);
	}

	@ExceptionHandler(UserExistedException.class)
	public ResponseEntity userExistedException(UserExistedException ex, WebRequest request) {
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UserNotFoundException.class)
	public ResponseEntity<ResponseDTO> userNotFoundException(UserNotFoundException ex, WebRequest request) {
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.NOT_FOUND);
	}

	@ExceptionHandler(InvalidPasswordException.class)
	public ResponseEntity<ResponseDTO> invalidPasswordException (InvalidPasswordException ex, WebRequest request){
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(SamePasswordException.class)
	public ResponseEntity<ResponseDTO> samePasswordException (SamePasswordException ex, WebRequest request){
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(UserSignupException.class)
	public ResponseEntity userSignupException(UserSignupException ex, WebRequest request) {
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	/** Global Exception **/
	@ExceptionHandler(Exception.class)
	public ResponseEntity<?> globleExcpetionHandler(Exception ex, WebRequest request) {
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}

	@ExceptionHandler(WrongPasswordOrUsernameException.class)
	public ResponseEntity<ResponseDTO> wrongPasswordOrUsernameHandler (WrongPasswordOrUsernameException ex, WebRequest request){
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(InvalidOldPassword.class)
	public ResponseEntity<ResponseDTO> wrongOldPasswordHandler (InvalidOldPassword ex, WebRequest request){
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}

	@ExceptionHandler(ChangePasswordFailedException.class)
	public ResponseEntity<ResponseDTO> changePasswordFailedHandle (ChangePasswordFailedException ex, WebRequest request){
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.INTERNAL_SERVER_ERROR);
	}
	
	@ExceptionHandler(StaffCodeNotExistsException.class)
	public ResponseEntity<ResponseDTO> StaffCodeNotExistsException (StaffCodeNotExistsException ex, WebRequest request){
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(UserIsDeletedException.class)
	public ResponseEntity<ResponseDTO> UserIsDeletedException (UserIsDeletedException ex, WebRequest request){
		ResponseDTO errorResponse = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	public ResponseEntity<ResponseDTO> AssetAssignedException(AssetAssignedException ex, WebRequest request) {
		ResponseDTO responseDTO = new ResponseDTO(ex.getMessage(), null, null);
		return new ResponseEntity<>(responseDTO, HttpStatus.BAD_REQUEST);
	}
}
