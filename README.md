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
    <td>^"([^"]|\\")*"$</td>
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
    <td><Goal\></td>
    <td><List\></td>
    <td>NAME, EOF</td>
  </tr>
  <tr>
    <td>2</td>
    <td rowspan="2"><List\></td>
    <td>NAME <Object\> <List\></td>
    <td>NAME</td>
  </tr>
  <tr>
    <td>3</td>
    <td>EPSILON</td>
    <td>EOF</td>
  </tr>
  <tr>
    <td>4</td>
    <td rowspan="2"><Object\></td>
    <td>LEFT_BRACE <List\> RIGHT_BRACE</td>
    <td>LEFT_BRACE</td>
  </tr>
  <tr>
    <td>5</td>
    <td>COLON <Value\></td>
    <td>COLON</td>
  </tr>
  <tr>
    <td>6</td>
    <td rowspan="4"><Value\></td>
    <td>INTEGER</td>
    <td>INTEGER</td>
  </tr>
  <tr>
    <td>7</td>
    <td>DECIMAL</td>
    <td>DECIMAL</td>
  </tr>
  <tr>
    <td>7</td>
    <td>BOOLEAN</td>
    <td>BOOLEAN</td>
  </tr>
  <tr>
    <td>7</td>
    <td>STRING</td>
    <td>STRING</td>
  </tr>
</table>
