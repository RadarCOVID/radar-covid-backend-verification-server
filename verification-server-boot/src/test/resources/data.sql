DELETE FROM VERIFICATION.TAN;
DELETE FROM VERIFICATION.CCAA;

INSERT INTO VERIFICATION.CCAA (DE_CCAA_ID, DE_CCAA_NAME, DE_CCAA_PUBLIC_KEY, DE_CCAA_ISSUER) VALUES
('99', 'RADARCOVID', 'LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlHYk1CQUdCeXFHU000OUFnRUdCU3VCQkFBakE0R0dBQVFCbUlXU0ptdGVGNkh2VnI0M1V5SzliZStlNkpPQgpDRjlVaXpMeis4a3padkVEc25nMGl3VEF3UVB0QzdBMDlzQjVMM3EwSUl1N250Yzd4U1VqSUdTakZvd0JXL0xPCnFtMTBYQ1NkUWNZT3BMTi85dUI1emZKVUZOY3B6Ynk4dDAzSlg3TUZiYi9vQm1pcFNNNHptSm1UajR3Qm9XZ2sKRlF6ZEJHcnAwR2laUU9WVXRtUT0KLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==', 'RADARCOVID'),
('01', 'Andalucía', 'LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlHYk1CQUdCeXFHU000OUFnRUdCU3VCQkFBakE0R0dBQVFBbnZXL1Y5T2VZR0xUbWpyQUFBWmJOQ0dvMVBCcQppMllpK2NKbnNoMVNZNmRUT3RxT0JVa0VJNmVMQU51MnRtanI3TWJrOExrWG0vMlA4aXBSZEVoRHpsZ0EycnV2CklwNUpvQlRnM2ErWTNXdnFrOS80UFJJaURRNDJ4cFFLV01jbUhQRUtYM1VXUHZCejUvS0JueTU1MDlvVXdzdmwKL05MYlBsQXN5c3VOUW9WWnJTUT0KLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==', ''),
('02', 'Aragón', '', ''),
('03', 'Asturias, Principado de', '', ''),
('04', 'Balears, Illes', '', ''),
('05', 'Canarias', '', ''),
('06', 'Cantabria', '', ''),
('07', 'Castilla y León', '', ''),
('08', 'Castilla La Mancha', '', ''),
('09', 'Cataluña', '', ''),
('10', 'Comunitat Valenciana', '', ''),
('11', 'Extremadura', '', ''),
('12', 'Galicia', '', ''),
('13', 'Madrid, Comunidad de', '', ''),
('14', 'Murcia, Región de', '', ''),
('15', 'Navarra, Comunidad Foral de', '', ''),
('16', 'País Vasco', '', ''),
('17', 'Rioja, La', '', ''),
('18', 'Ceuta', '', ''),
('19', 'Melilla', '', '')
;
