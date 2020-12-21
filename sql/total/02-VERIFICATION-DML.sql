DELETE FROM VERIFICATION.CODE;

INSERT INTO VERIFICATION.CCAA (DE_CCAA_ID, DE_CCAA_NAME, DE_CCAA_PUBLIC_KEY, DE_CCAA_ISSUER) VALUES
('00', 'RADARCOVID', 'LS0tLS1CRUdJTiBQVUJMSUMgS0VZLS0tLS0KTUlHYk1CQUdCeXFHU000OUFnRUdCU3VCQkFBakE0R0dBQVFBbnZXL1Y5T2VZR0xUbWpyQUFBWmJOQ0dvMVBCcQppMllpK2NKbnNoMVNZNmRUT3RxT0JVa0VJNmVMQU51MnRtanI3TWJrOExrWG0vMlA4aXBSZEVoRHpsZ0EycnV2CklwNUpvQlRnM2ErWTNXdnFrOS80UFJJaURRNDJ4cFFLV01jbUhQRUtYM1VXUHZCejUvS0JueTU1MDlvVXdzdmwKL05MYlBsQXN5c3VOUW9WWnJTUT0KLS0tLS1FTkQgUFVCTElDIEtFWS0tLS0tCg==', 'RADARCOVID'),
('01', 'Andalucía', '', ''),
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

INSERT INTO VERIFICATION.CCAA_AUTH (DE_CCAA_ID, DE_AUTH) VALUES
('01', 'GENERATION'),
('02', 'GENERATION'),
('03', 'GENERATION'),
('04', 'GENERATION'),
('05', 'GENERATION'),
('06', 'GENERATION'),
('07', 'GENERATION'),
('08', 'GENERATION'),
('09', 'GENERATION'),
('10', 'GENERATION'),
('11', 'GENERATION'),
('12', 'GENERATION'),
('13', 'GENERATION'),
('14', 'GENERATION'),
('15', 'GENERATION'),
('16', 'GENERATION'),
('17', 'GENERATION'),
('18', 'GENERATION'),
('19', 'GENERATION')
;
