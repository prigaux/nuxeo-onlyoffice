<!DOCTYPE html>
<html>
<head>
  <title> </title>
  <link rel="stylesheet" href="${skinPath}/css/error.css" type="text/css" media="screen" charset="utf-8"/>

  <script language="javascript" type="text/javascript">
    function logonError() {
      window.location='${securidLogonUrl}';
    }
  </script>
</head>
    <body>
    	<div class="container">
    <h1>Vous n'avez pas l'autorisation nécessaire pour effectuer l'action demandée.</h1>

      <p>Une authentification forte par clé OTP est requise</p>
      <div class="links">

        <a class="block back" href="javascript:window.history.back();">
          <span>Revenir à la page précédente</span>
        </a>
        <a class="block change" href="javascript:logonError();">
          <span>Se ré-authentifier avec une clé OTP</span>
        </a>
	  </div>

  </div>
    </body>
</html>