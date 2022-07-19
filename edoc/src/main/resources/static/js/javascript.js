function validaPdf() {
			var fileInput = document.getElementById('file');
			
			var filePath = fileInput.value;
		
			// Permite somente arquivo PDF
			var allowedExtensions = /(\.pdf)$/i;
			
			if (!allowedExtensions.exec(filePath)) {
				alert('Tipo de arquivo inválido! Somente arquivos do tipo PDF');
				fileInput.value = '';
				return false;
			}
		}