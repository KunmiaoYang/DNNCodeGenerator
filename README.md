# DNN Code Generator

## Introduction

## Usage

## Regular Expressions for Token
<table>
  <tr>
    <th>Token</th>
    <th>Regular Expression</th>
  </tr>
  <tr>
    <td>STRING</td>
    <td>^&quot;([^&quot;]|\\&quot;)\*&quot;$</td>
  </tr>
</table>

## Grammar
<table>
  <tr>
    <th>Rule</th>
    <th>Non-terminal</th>
    <th>Production</th>
    <th>First plus</th>
  </tr>
  <tr>
    <td>1</td>
    <td>&lt;Goal&gt;</td>
    <td>&lt;List&gt;</td>
    <td>NAME, EOF</td>
  </tr>
  <tr>
    <td>2</td>
    <td rowspan="2">&lt;List&gt;</td>
    <td>NAME &lt;Object&gt; &lt;List&gt;</td>
    <td>NAME</td>
  </tr>
  <tr>
    <td>3</td>
    <td>EPSILON</td>
    <td>RIGHT_BRACE, EOF</td>
  </tr>
  <tr>
    <td>4</td>
    <td rowspan="2">&lt;Object&gt;</td>
    <td>LEFT_BRACE &lt;List&gt; RIGHT_BRACE</td>
    <td>LEFT_BRACE</td>
  </tr>
  <tr>
    <td>5</td>
    <td>COLON &lt;Value&gt;</td>
    <td>COLON</td>
  </tr>
  <tr>
    <td>6</td>
    <td rowspan="5">&lt;Value&gt;</td>
    <td>INTEGER</td>
    <td>INTEGER</td>
  </tr>
  <tr>
    <td>7</td>
    <td>FLOAT</td>
    <td>FLOAT</td>
  </tr>
  <tr>
    <td>8</td>
    <td>BOOLEAN</td>
    <td>BOOLEAN</td>
  </tr>
  <tr>
    <td>9</td>
    <td>STRING</td>
    <td>STRING</td>
  </tr>
  <tr>
    <td>10</td>
    <td>SPECIAL</td>
    <td>SPECIAL</td>
  </tr>
</table>
